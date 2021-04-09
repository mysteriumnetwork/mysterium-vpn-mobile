package updated.mysterium.vpn.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.UserAttributes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import mysterium.RegisterIdentityRequest
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SplashViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "SplashViewModel"
        const val NODE_IDENTITY_KEY = "node_identity"
    }

    val navigateForward: LiveData<Unit>
        get() = _navigateForward

    private val _navigateForward = MutableLiveData<Unit>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val termsUseCase = useCaseProvider.terms()
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
        service
    }

    fun isUserAlreadyLogin() = loginUseCase.isAlreadyLogin()

    fun isTermsAccepted() = termsUseCase.isTermsAccepted()

    fun isAccountFlowShown() = loginUseCase.isAccountFlowShown()

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
            loadIdentity()
        }
    }

    private suspend fun loadIdentity() {
        val nodeIdentity = connectionUseCase.getIdentity()
        val identity = IdentityModel(
            address = nodeIdentity.address,
            channelAddress = nodeIdentity.channelAddress,
            status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
        )
        registerIntercomClient(identity.address)
        registerIdentity(identity)
    }

    private fun registerIntercomClient(address: String) {
        Intercom.client().apply {
            registerUnidentifiedUser()
            val attrs = UserAttributes.Builder()
                .withCustomAttribute(NODE_IDENTITY_KEY, address)
                .build()
            updateUser(attrs)
        }
    }

    private suspend fun registerIdentity(identity: IdentityModel) {
        if (!identity.registered) {
            val req = RegisterIdentityRequest().apply {
                identityAddress = identity.address
            }
            val handler = CoroutineExceptionHandler { _, exception ->
                Log.e(TAG, "Identity registration error")
            }
            viewModelScope.launch(handler) {
                connectionUseCase.registerIdentity(req)
            }
        }
        loadRegistrationFees()
    }

    private suspend fun loadRegistrationFees() {
        connectionUseCase.registrationFees()
        if (isAnimationLoaded) {
            _navigateForward.postValue(Unit)
        } else {
            isDataLoaded = true
        }
    }
}
