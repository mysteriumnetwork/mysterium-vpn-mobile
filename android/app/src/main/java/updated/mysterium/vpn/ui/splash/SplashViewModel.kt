package updated.mysterium.vpn.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
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

    private val connectionUseCase = useCaseProvider.connection()
    private var isTimerFinished = false
    private var isDataLoaded = false
    private val deferredNode = DeferredNode()

    private var _navigateToOnboarding = MutableLiveData<String>()
    val navigateToOnboarding
        get() = _navigateToOnboarding

    fun startLoading(deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        startTimer()
        viewModelScope.launch {
            startDeferredNode(deferredMysteriumCoreService)
        }
    }

    private suspend fun startDeferredNode(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>
    ) {
        viewModelScope.launch {
            deferredMysteriumCoreService.await()
            if (!deferredNode.startedOrStarting()) {
                deferredNode.start(deferredMysteriumCoreService.await())
            }
            initRepository()
        }
    }

    private suspend fun initRepository() {
        connectionUseCase.initDeferredNode(deferredNode)
        loadIdentity()
    }

    private fun startTimer() {
        Timer().schedule(timerTask {
            if (isDataLoaded) {
                _navigateToOnboarding.postValue("")
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
        registerIdentity(identity)
    }

    private suspend fun registerIdentity(identity: IdentityModel) {
        if (!identity.registered) {
            val req = RegisterIdentityRequest().apply {
                identityAddress = identity.address
            }
            connectionUseCase.registerIdentity(req)
        }
        loadRegistrationFees()
    }

    private suspend fun loadRegistrationFees() {
        connectionUseCase.registrationFees()
        if (isTimerFinished) {
            _navigateToOnboarding.postValue("")
        } else {
            isDataLoaded = true
        }
    }

    companion object {
        private const val ONE_SECOND_DELAY = 1000L
    }
}
