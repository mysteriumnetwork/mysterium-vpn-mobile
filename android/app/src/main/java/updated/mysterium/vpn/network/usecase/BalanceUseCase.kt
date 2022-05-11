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

    suspend fun forceBalanceUpdate(
        getBalanceRequest: GetBalanceRequest
    ) = nodeRepository.forceBalanceUpdate(getBalanceRequest)

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

    fun isMinBalancePopUpShown() = sharedPreferencesManager.getBoolPreferenceValue(
        SharedPreferencesList.MIN_BALANCE
    )

    fun minBalancePopUpShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.MIN_BALANCE,
        value = true
    )

    fun clearMinBalancePopUpHistory() {
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.MIN_BALANCE)
    }

    fun isFirstBalancePushShown() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.FIRST_BALANCE_PUSH
    )

    fun firstBalancePushShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.FIRST_BALANCE_PUSH,
        value = true
    )

    fun isSecondBalancePushShown() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.SECOND_BALANCE_PUSH
    )

    fun secondBalancePushShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.SECOND_BALANCE_PUSH,
        value = true
    )

    fun isThirdBalancePushShown() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.THIRD_BALANCE_PUSH
    )

    fun thirdBalancePushShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.THIRD_BALANCE_PUSH,
        value = true
    )

    fun clearBalancePushHistory() {
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.FIRST_BALANCE_PUSH)
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.SECOND_BALANCE_PUSH)
        sharedPreferencesManager.removePreferenceValue(SharedPreferencesList.THIRD_BALANCE_PUSH)
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
