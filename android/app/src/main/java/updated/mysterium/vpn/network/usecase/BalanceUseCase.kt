package updated.mysterium.vpn.network.usecase

import mysterium.GetBalanceRequest
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class BalanceUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    private companion object {
        const val CURRENCY = "USD"
    }

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    suspend fun getBalance(
        getBalanceRequest: GetBalanceRequest
    ) = nodeRepository.balance(getBalanceRequest)

    suspend fun initBalanceListener(
        callback: (Double) -> Unit
    ) = nodeRepository.registerBalanceChangeCallback(callback)

    suspend fun getUsdEquivalent() = nodeRepository.getExchangeRate(CURRENCY)

    fun isBalancePopUpShown() = sharedPreferencesManager.getPreferenceValue(
        SharedPreferencesList.BALANCE
    )

    fun balancePopUpShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.BALANCE,
        value = true
    )
}
