package updated.mysterium.vpn.ui.prepare.top.up

import androidx.lifecycle.ViewModel
import mysterium.RegisterIdentityRequest
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class PrepareTopUpViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val loginUseCase = useCaseProvider.login()
    private val tokenUseCase = useCaseProvider.token()
    private val connectionUseCase = useCaseProvider.connection()

    fun accountFlowShown() {
        loginUseCase.accountFlowShown()
    }

    fun getRegistrationTokenReward(token: String) = liveDataResult {
        tokenUseCase.getRegistrationTokenReward(token)
    }

    fun registerIdentityWithoutToken() = liveDataResult {
        identityRegistration()
    }

    fun registerIdentity(token: String) = liveDataResult {
        identityRegistration(token)
    }

    private suspend fun identityRegistration(token: String? = null) {
        val nodeIdentity = connectionUseCase.getIdentity()
        val identity = IdentityModel(
            address = nodeIdentity.address,
            channelAddress = nodeIdentity.channelAddress,
            status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
        )
        if (!identity.registered) {
            val req = RegisterIdentityRequest().apply {
                identityAddress = identity.address
                token?.let {
                    this.token = it
                }
            }
            connectionUseCase.registerIdentity(req)
            loadRegistrationFees()
        }
    }

    private suspend fun loadRegistrationFees() {
        connectionUseCase.registrationFees()
    }
}
