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
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SplashViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "SplashViewModel"
    }

    val navigateForward: LiveData<Unit>
        get() = _navigateForward

    val preloadFinished: LiveData<Unit>
        get() = _preloadFinished

    val nodeStartingError: LiveData<Exception>
        get() = _nodeStartingError

    private val _nodeStartingError = MutableLiveData<Exception>()
    private val _preloadFinished = SingleLiveEvent<Unit>()
    private val _navigateForward = MutableLiveData<Unit>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val termsUseCase = useCaseProvider.terms()
    private val settingsUseCase = useCaseProvider.settings()
    private val pushyUseCase = useCaseProvider.pushy()
    private var deferredNode = DeferredNode()
    private var service: MysteriumCoreService? = null
    private var isAnimationLoaded = false
    private var isDataLoaded = false
    private var isNavigateForward = false

    fun startLoading(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>
    ) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            _nodeStartingError.postValue(exception as Exception?)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            service = deferredMysteriumCoreService.await()
            if (
                service?.getDeferredNode() != null &&
                service?.getDeferredNode()?.startedOrStarting() == true
            ) {
                service?.getDeferredNode()?.let {
                    deferredNode = it
                    _preloadFinished.postValue(Unit)
                }
            } else {
                service?.let {
                    it.stopNode()
                    deferredNode
                    deferredNode.start(it) { exception ->
                        if (exception != null) {
                            _nodeStartingError.postValue(exception)
                        } else {
                            _preloadFinished.postValue(Unit)
                        }
                    }
                }
            }
        }
    }

    fun isUserAlreadyLogin() = loginUseCase.isAlreadyLogin()

    fun isTermsAccepted() = termsUseCase.isTermsAccepted()

    fun isAccountCreated() = loginUseCase.isAccountCreated()

    fun isTopUpFlowShown() = loginUseCase.isTopFlowShown()

    fun isNewUser() = loginUseCase.isNewUser()

    fun animationLoaded() {
        if (isDataLoaded) {
            if (!isNavigateForward) {
                isNavigateForward = true
                _navigateForward.postValue(Unit)
            }
        } else {
            isAnimationLoaded = true
        }
    }

    fun initRepository() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            _nodeStartingError.postValue(exception as Exception?)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            service?.subscribeToListeners()
            balanceUseCase.initDeferredNode(deferredNode)
            connectionUseCase.initDeferredNode(deferredNode)
            if (isAnimationLoaded) {
                if (!isNavigateForward) {
                    isNavigateForward = true
                    _navigateForward.postValue(Unit)
                }
            } else {
                isDataLoaded = true
            }
        }
    }

    fun getUserSavedMode() = settingsUseCase.getUserDarkMode()

    fun getLastCryptoCurrency() = pushyUseCase.getCryptoCurrency()

    fun getIdentityAddress() = liveDataResult {
        connectionUseCase.getIdentityAddress()
    }

    fun getIdentity() = liveDataResult {
        IdentityModel(connectionUseCase.getIdentity())
    }
}
