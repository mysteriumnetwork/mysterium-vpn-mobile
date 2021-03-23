package updated.mysterium.vpn.network.usecase

import network.mysterium.service.core.NodeRepository

class PrivateKeyUseCase(private val nodeRepository: NodeRepository) {

    suspend fun downloadPrivateKey(
        identityAddress: String, newPassphrase: String
    ) = nodeRepository.downloadPrivateKey(identityAddress, newPassphrase)
}
