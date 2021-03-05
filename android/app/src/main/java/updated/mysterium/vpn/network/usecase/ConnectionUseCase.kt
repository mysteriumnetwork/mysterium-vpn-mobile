package updated.mysterium.vpn.network.usecase

import mysterium.ConnectRequest
import mysterium.GetBalanceRequest
import mysterium.RegisterIdentityRequest
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository
import network.mysterium.service.core.Statistics

class ConnectionUseCase(private val nodeRepository: NodeRepository) {

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    suspend fun connect(connectRequest: ConnectRequest) = nodeRepository.connect(connectRequest)

    suspend fun getIdentity() = nodeRepository.getIdentity()

    suspend fun registerIdentity(
        registerIdentityRequest: RegisterIdentityRequest
    ) = nodeRepository.registerIdentity(registerIdentityRequest)

    suspend fun registrationFees() = nodeRepository.identityRegistrationFees()

    suspend fun status() = nodeRepository.status()

    suspend fun registerStatisticsChangeCallback(
        callback: (Statistics) -> Unit
    ) = nodeRepository.registerStatisticsChangeCallback(callback)

    suspend fun connectionStatusCallback(
        callback: (String) -> Unit
    ) = nodeRepository.registerConnectionStatusChangeCallback(callback)

    suspend fun disconnect() = nodeRepository.disconnect()
}
