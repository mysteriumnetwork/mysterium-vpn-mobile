package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.NodeRepository

class LocationUseCase(private val nodeRepository: NodeRepository) {

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    suspend fun getLocation() = nodeRepository.location()
}
