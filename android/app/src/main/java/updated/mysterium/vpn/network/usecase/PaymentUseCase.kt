package updated.mysterium.vpn.network.usecase

import com.google.gson.Gson
import mysterium.CreateOrderRequest
import mysterium.CreatePaymentGatewayOrderReq
import mysterium.OrderUpdatedCallbackPayload
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.model.payment.CardinityGateway
import updated.mysterium.vpn.model.payment.Order

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

    suspend fun createPaymentGatewayOrder(
        country: String,
        identityAddress: String,
        mystAmount: Double,
        currency: String,
    ): Order {
        val req = CreatePaymentGatewayOrderReq().apply {
            this.country = country
            this.payCurrency = currency
            this.identityAddress = identityAddress
            this.mystAmount = mystAmount.toString()
            this.gateway = "cardinity"
            this.gatewayCallerData = Gson()
                .toJson(CardinityGateway("US"))
                .toString()
                .toByteArray()
        }
        return nodeRepository.createPaymentGatewayOrder(req)
    }

    suspend fun paymentOrderCallback(
        action: (OrderUpdatedCallbackPayload) -> Unit
    ) = nodeRepository.registerOrderUpdatedCallback {
        action.invoke(it)
    }

    suspend fun getGateways() = nodeRepository.getGateways()
}
