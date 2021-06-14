package updated.mysterium.vpn.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SplashViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "SplashViewModel"
    }

    val navigateForward: LiveData<Unit>
        get() = _navigateForward

    val preloadFinished: LiveData<Unit>
        get() = _preloadFinished

    private val _preloadFinished = SingleLiveEvent<Unit>()
    private val _navigateForward = MutableLiveData<Unit>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val termsUseCase = useCaseProvider.terms()
    private val settingsUseCase = useCaseProvider.settings()
    private val pushyUseCase = useCaseProvider.pushy()
    private var isAnimationLoaded = false
    private var isDataLoaded = false
    private var deferredNode = DeferredNode()

    fun startLoading(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>
    ) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            val service = deferredMysteriumCoreService.await()
            if (service.getDeferredNode() != null) {
                service.getDeferredNode()?.let {
                    deferredNode = it
                }
            } else {
                if (!deferredNode.startedOrStarting()) {
                    deferredNode.start(service)
                }
            }
            _preloadFinished.call()
        }
    }

    fun isUserAlreadyLogin() = loginUseCase.isAlreadyLogin()

    fun isTermsAccepted() = termsUseCase.isTermsAccepted()

    fun isAccountCreated() = loginUseCase.isAccountCreated()

    fun isTopUpFlowShown() = loginUseCase.isTopFlowShown()

    fun isNewUser() = loginUseCase.isNewUser()

    fun animationLoaded() {
        if (isDataLoaded) {
            _navigateForward.postValue(Unit)
        } else {
            isAnimationLoaded = true
        }
    }

    fun initRepository() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            balanceUseCase.initDeferredNode(deferredNode)
            connectionUseCase.initDeferredNode(deferredNode)
            if (isAnimationLoaded) {
                _navigateForward.postValue(Unit)
            } else {
                isDataLoaded = true
            }
        }
    }

    fun getUserSavedMode() = settingsUseCase.getUserDarkMode()

    fun getLastCryptoCurrency() = pushyUseCase.getCryptoCurrency()
}
