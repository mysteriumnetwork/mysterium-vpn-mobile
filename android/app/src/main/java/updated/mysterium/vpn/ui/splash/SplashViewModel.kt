package updated.mysterium.vpn.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.common.languages.LanguagesUtil
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SplashViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val navigateForward: LiveData<Unit>
        get() = _navigateForward

    private val _navigateForward = MutableLiveData<Unit>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val termsUseCase = useCaseProvider.terms()
    private val settingsUseCase = useCaseProvider.settings()
    private var isAnimationLoaded = false
    private var isDataLoaded = false
    private var deferredNode = DeferredNode()

    fun startLoading(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>
    ) = liveDataResult {
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
        deferredNode
    }

    fun isUserAlreadyLogin() = loginUseCase.isAlreadyLogin()

    fun isTermsAccepted() = termsUseCase.isTermsAccepted()

    fun isAccountCreated() = loginUseCase.isAccountCreated()

    fun isTopUpFlowShown() = loginUseCase.isTopFlowShown()

    fun animationLoaded() {
        if (isDataLoaded) {
            _navigateForward.postValue(Unit)
        } else {
            isAnimationLoaded = true
        }
    }

    fun initRepository() {
        viewModelScope.launch {
            balanceUseCase.initDeferredNode(deferredNode)
            connectionUseCase.initDeferredNode(deferredNode)
            if (isAnimationLoaded) {
                _navigateForward.postValue(Unit)
            } else {
                isDataLoaded = true
            }
        }
    }

    fun initUserLocaleLanguage(countryCode: String) = settingsUseCase.userInitialCountryLanguage(
        countryCode = LanguagesUtil.convertUserLanguage(countryCode)
    )
}
