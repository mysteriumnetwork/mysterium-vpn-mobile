package updated_mysterium_vpn.network.provider.usecase

import network.mysterium.service.core.NodeRepository
import updated_mysterium_vpn.database.AppDatabase
import updated_mysterium_vpn.network.usecase.LocationUseCase
import updated_mysterium_vpn.network.usecase.NodesUseCase

class UseCaseProvider(
        private val nodeRepository: NodeRepository,
        private val database: AppDatabase
) {

    fun nodes() = NodesUseCase(nodeRepository, database.nodeDao())

    fun location() = LocationUseCase(nodeRepository)
}
