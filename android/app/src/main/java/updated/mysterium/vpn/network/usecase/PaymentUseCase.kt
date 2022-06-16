package updated.mysterium.vpn.network.usecase

import mysterium.CreatePaymentGatewayOrderReq
import mysterium.OrderUpdatedCallbackPayload
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.Order
import updated.mysterium.vpn.model.payment.Purchase

class PaymentUseCase(private val nodeRepository: NodeRepository) {

    companion object {
        private const val currency = "USD"
    }

    suspend fun createPaymentGatewayOrder(
        identityAddress: String,
        amountUSD: Double
    ): Order {
        val req = CreatePaymentGatewayOrderReq().apply {
            this.payCurrency = currency
            this.identityAddress = identityAddress
            this.amountUSD = amountUSD.toString()
            this.gateway = Gateway.GOOGLE.gateway
        }
        return nodeRepository.createPaymentGatewayOrder(req)
    }

    suspend fun paymentOrderCallback(
        action: (OrderUpdatedCallbackPayload) -> Unit
    ) = nodeRepository.registerOrderUpdatedCallback {
        action.invoke(it)
    }

    suspend fun getGateways() = nodeRepository.getGateways()

    suspend fun isBalanceLimitExceeded() = nodeRepository.isBalanceLimitExceeded()

    suspend fun gatewayClientCallback(purchase: Purchase) =
        nodeRepository.gatewayClientCallback(purchase)
}
