package updated_mysterium_vpn.network.provider.usecase

import network.mysterium.service.core.NodeRepository
import updated_mysterium_vpn.network.usecase.NodesUseCase

class UseCaseProvider(private val nodeRepository: NodeRepository) {

    fun nodes() = NodesUseCase(nodeRepository)
}
