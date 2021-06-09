package updated.mysterium.vpn.network.provider.usecase

import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.AppDatabase
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.network.usecase.*

class UseCaseProvider(
    private val nodeRepository: NodeRepository,
    private val database: AppDatabase,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    fun nodes() = NodesUseCase(nodeRepository, database.nodeDao())

    fun location() = LocationUseCase(nodeRepository)

    fun connection() = ConnectionUseCase(nodeRepository, sharedPreferencesManager)

    fun login() = LoginUseCase(sharedPreferencesManager)

    fun balance() = BalanceUseCase(nodeRepository, sharedPreferencesManager)

    fun report() = ReportUseCase(nodeRepository)

    fun statistic() = StatisticUseCase(nodeRepository, sharedPreferencesManager)

    fun terms() = TermsUseCase(sharedPreferencesManager)

    fun privateKey() = PrivateKeyUseCase(nodeRepository)

    fun payment() = PaymentUseCase(nodeRepository)

    fun settings() = SettingsUseCase(nodeRepository, sharedPreferencesManager)

    fun filters() = FilterUseCase(nodeRepository, sharedPreferencesManager)

    fun token() = TokenUseCase(nodeRepository)

    fun pushy() = PushyUseCase(sharedPreferencesManager)
}
