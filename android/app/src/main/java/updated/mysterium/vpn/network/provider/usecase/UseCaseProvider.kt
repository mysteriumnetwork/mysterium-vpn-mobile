package updated.mysterium.vpn.network.provider.usecase

import android.content.Context
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.database.AppDatabase
import updated.mysterium.vpn.network.usecase.*

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

    fun report() = ReportUseCase(nodeRepository)

    fun statistic() = StatisticUseCase(nodeRepository)

    fun terms() = TermsUseCase(context)

    fun privateKey() = PrivateKeyUseCase(nodeRepository)
}
