package updated.mysterium.vpn.network.usecase

import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository

class LocationUseCase(private val nodeRepository: NodeRepository) {

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    suspend fun getLocation() = nodeRepository.location()
}
