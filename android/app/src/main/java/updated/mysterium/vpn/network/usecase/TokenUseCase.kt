package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.core.NodeRepository

class TokenUseCase(private val nodeRepository: NodeRepository) {

    suspend fun getRegistrationTokenReward(
        registrationToken: String
    ) = nodeRepository.getRegistrationTokenReward(registrationToken)
}
