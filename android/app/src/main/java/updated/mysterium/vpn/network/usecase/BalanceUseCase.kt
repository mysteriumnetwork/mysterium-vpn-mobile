package updated.mysterium.vpn.network.usecase

import android.content.Context
import mysterium.GetBalanceRequest
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository

class BalanceUseCase(private val nodeRepository: NodeRepository, private val context: Context) {

    private companion object {
        const val CURRENCY = "USD"
        const val BALANCE_POP_UP_PREFERENCES_KEY = "BALANCE_POP_UP"
        const val BALANCE_POP_UP_SHOWN_KEY = "BALANCE_POP_UP_SHOWN"
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

    fun isBalancePopUpShown() = context.getSharedPreferences(
        BALANCE_POP_UP_PREFERENCES_KEY, Context.MODE_PRIVATE
    ).contains(BALANCE_POP_UP_SHOWN_KEY)

    fun balancePopUpShown() = context
        .getSharedPreferences(BALANCE_POP_UP_PREFERENCES_KEY, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(BALANCE_POP_UP_SHOWN_KEY, true).apply()
}
