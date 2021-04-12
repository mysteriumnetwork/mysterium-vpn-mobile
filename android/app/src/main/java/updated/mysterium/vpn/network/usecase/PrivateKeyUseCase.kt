package updated.mysterium.vpn.network.usecase

import network.mysterium.service.core.NodeRepository

class PrivateKeyUseCase(private val nodeRepository: NodeRepository) {

    suspend fun downloadPrivateKey(
        identityAddress: String, newPassphrase: String
    ) = nodeRepository.downloadPrivateKey(identityAddress, newPassphrase)

    suspend fun exportIdentity(
        address: String, passphrase: String
    ) = nodeRepository.exportIdentity(address, passphrase)

    suspend fun importIdentity(
        privateKey: String, passphrase: String
    ) = nodeRepository.importIdentity(privateKey.toByteArray(), passphrase)
}
