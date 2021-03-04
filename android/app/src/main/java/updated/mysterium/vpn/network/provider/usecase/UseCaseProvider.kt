package updated.mysterium.vpn.network.provider.usecase

import android.content.Context
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.database.AppDatabase
import updated.mysterium.vpn.network.usecase.BalanceUseCase
import updated.mysterium.vpn.network.usecase.ConnectionUseCase
import updated.mysterium.vpn.network.usecase.LocationUseCase
import updated.mysterium.vpn.network.usecase.LoginUseCase
import updated.mysterium.vpn.network.usecase.NodesUseCase

class UseCaseProvider(
    private val nodeRepository: NodeRepository,
    private val database: AppDatabase,
    private val context: Context
) {

    fun nodes() = NodesUseCase(nodeRepository, database.nodeDao())

    fun location() = LocationUseCase(nodeRepository)

    fun connection() = ConnectionUseCase(nodeRepository)

    fun login() = LoginUseCase(context)

    fun balance() = BalanceUseCase(nodeRepository)
}
