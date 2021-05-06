package updated.mysterium.vpn.network.usecase

import network.mysterium.service.core.NodeRepository

class TokenUseCase(private val nodeRepository: NodeRepository) {

    suspend fun getRegistrationTokenReward(
        registrationToken: String
    ) = nodeRepository.getRegistrationTokenReward(registrationToken)
}
