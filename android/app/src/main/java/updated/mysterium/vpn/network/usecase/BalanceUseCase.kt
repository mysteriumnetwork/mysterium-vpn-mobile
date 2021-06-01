package updated.mysterium.vpn.network.usecase

import mysterium.GetBalanceRequest
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.NodeRepository
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

    suspend fun getWalletEquivalent(balance: Double) = nodeRepository.getWalletEquivalent(balance)

    fun isBalancePopUpShown() = sharedPreferencesManager.getBoolPreferenceValue(
        SharedPreferencesList.BALANCE
    )

    fun balancePopUpShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.BALANCE,
        value = true
    )

    fun clearBalancePopUpHistory() {
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.BALANCE)
    }

    fun isMinBalancePopUpShown() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.MIN_BALANCE
    )

    fun minBalancePopUpShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.MIN_BALANCE,
        value = true
    )

    fun clearMinBalancePopUpHistory() {
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.MIN_BALANCE)
    }

    fun isBalancePushShown() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.BALANCE_PUSH
    )

    fun balancePushShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.BALANCE_PUSH,
        value = true
    )

    fun clearBalancePushHistory() {
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.BALANCE_PUSH)
    }

    fun isMinBalancePushShown() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.MIN_BALANCE_PUSH
    )

    fun minBalancePushShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.MIN_BALANCE_PUSH,
        value = true
    )

    fun clearMinBalancePushHistory() {
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.MIN_BALANCE_PUSH)
    }
}
