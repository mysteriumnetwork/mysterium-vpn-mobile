package updated.mysterium.vpn.network.usecase

import mysterium.CreateOrderRequest
import mysterium.CreatePaymentGatewayOrderReq
import mysterium.OrderUpdatedCallbackPayload
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.Order

class PaymentUseCase(private val nodeRepository: NodeRepository) {

    companion object {
        private const val currency = "USD"
    }

    suspend fun createPaymentOrder(
        currency: String,
        identityAddress: String,
        mystAmount: Double,
        isLighting: Boolean
    ): Order {
        val req = CreateOrderRequest().apply {
            this.payCurrency = currency
            this.identityAddress = identityAddress
            this.mystAmount = mystAmount
            this.lightning = isLighting
        }
        return nodeRepository.createPaymentOrder(req)
    }

    suspend fun createPaymentGatewayOrder(
        identityAddress: String,
        amountUSD: Double
    ) {
        val req = CreatePaymentGatewayOrderReq().apply {
            this.payCurrency = currency
            this.identityAddress = identityAddress
            this.mystAmount = "0"
            this.amountUSD = amountUSD.toString()
            this.gateway = Gateway.GOOGLE.gateway
        }
        nodeRepository.createPaymentGatewayOrder(req)
    }

    suspend fun paymentOrderCallback(
        action: (OrderUpdatedCallbackPayload) -> Unit
    ) = nodeRepository.registerOrderUpdatedCallback {
        action.invoke(it)
    }

    suspend fun getGateways() = nodeRepository.getGateways()

    suspend fun isBalanceLimitExceeded() = nodeRepository.isBalanceLimitExceeded()
}
