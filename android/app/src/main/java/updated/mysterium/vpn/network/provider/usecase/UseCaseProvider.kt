package updated.mysterium.vpn.network.provider.usecase

import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.database.AppDatabase
import updated.mysterium.vpn.network.usecase.LocationUseCase
import updated.mysterium.vpn.network.usecase.NodesUseCase

class UseCaseProvider(
    private val nodeRepository: NodeRepository,
    private val database: AppDatabase
) {

    fun nodes() = NodesUseCase(nodeRepository, database.nodeDao())

    fun location() = LocationUseCase(nodeRepository)
}
