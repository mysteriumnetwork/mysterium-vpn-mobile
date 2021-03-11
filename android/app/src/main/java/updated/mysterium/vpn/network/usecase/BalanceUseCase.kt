package updated.mysterium.vpn.network.usecase

import mysterium.GetBalanceRequest
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository

class BalanceUseCase(private val nodeRepository: NodeRepository) {

    fun initDeferredNode(deferredNode: DeferredNode) {
        nodeRepository.deferredNode = deferredNode
    }

    suspend fun getBalance(
        getBalanceRequest: GetBalanceRequest
    ) = nodeRepository.balance(getBalanceRequest)

    suspend fun initBalanceListener(
        callback: (Double) -> Unit
    ) = nodeRepository.registerBalanceChangeCallback(callback)
}
