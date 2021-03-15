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
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.util.Timer
import kotlin.concurrent.timerTask

class SplashViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "SplashViewModel"
        const val ONE_SECOND_DELAY = 1000L
        const val NODE_IDENTITY_KEY = "node_identity"
    }

    val navigateForward: LiveData<String>
        get() = _navigateForward

    private val _navigateForward = MutableLiveData<String>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val termsUseCase = useCaseProvider.terms()
    private var isTimerFinished = false
    private var isDataLoaded = false
    private var deferredNode = DeferredNode()

    fun startLoading(deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        startTimer()
        viewModelScope.launch {
            startDeferredNode(deferredMysteriumCoreService)
        }
    }

    fun isUserAlreadyLogin() = loginUseCase.isAlreadyLogin()

    fun isTermsAccepted() = termsUseCase.isTermsAccepted()

    private suspend fun startDeferredNode(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>
    ) {
        viewModelScope.launch {
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
            initRepository()
        }
    }

    private suspend fun initRepository() {
        balanceUseCase.initDeferredNode(deferredNode)
        connectionUseCase.initDeferredNode(deferredNode)
        loadIdentity()
    }

    private fun startTimer() {
        Timer().schedule(timerTask {
            if (isDataLoaded) {
                _navigateForward.postValue("")
            } else {
                isTimerFinished = true
            }
        }, ONE_SECOND_DELAY)
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
        if (isTimerFinished) {
            _navigateForward.postValue("")
        } else {
            isDataLoaded = true
        }
    }
}