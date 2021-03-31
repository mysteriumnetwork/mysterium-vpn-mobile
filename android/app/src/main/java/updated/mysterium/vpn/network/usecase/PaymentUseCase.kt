package updated.mysterium.vpn.network.usecase

import mysterium.CreateOrderRequest
import mysterium.OrderUpdatedCallbackPayload
import network.mysterium.payment.Order
import network.mysterium.service.core.NodeRepository

class PaymentUseCase(private val nodeRepository: NodeRepository) {

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

    suspend fun paymentOrderCallback(
        action: (OrderUpdatedCallbackPayload) -> Unit
    ) = nodeRepository.registerOrderUpdatedCallback {
        action.invoke(it)
    }
}
