package updated.mysterium.vpn.network.usecase

import com.google.gson.Gson
import mysterium.CreatePaymentGatewayOrderReq
import mysterium.OrderUpdatedCallbackPayload
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.model.payment.*

class PaymentUseCase(private val nodeRepository: NodeRepository) {

    suspend fun createCoingatePaymentGatewayOrder(
        currency: String,
        identityAddress: String,
        mystAmount: Double,
        isLightning: Boolean
    ): Order {
        val req = CreatePaymentGatewayOrderReq().apply {
            this.payCurrency = currency
            this.identityAddress = identityAddress
            this.mystAmount = mystAmount.toString()
            this.gateway = Gateway.COINGATE.gateway
            this.gatewayCallerData = Gson()
                .toJson(Lightning(isLightning))
                .toString()
                .toByteArray()
        }
        return nodeRepository.createCoingatePaymentGatewayOrder(req)
    }

    suspend fun createCardinityPaymentGatewayOrder(
        country: String,
        identityAddress: String,
        mystAmount: Double,
        currency: String,
    ): CardOrder {
        val req = CreatePaymentGatewayOrderReq().apply {
            this.country = country
            this.payCurrency = currency
            this.identityAddress = identityAddress
            this.mystAmount = mystAmount.toString()
            this.gateway = Gateway.CARDINITY.gateway
            this.gatewayCallerData = Gson()
                .toJson(CardinityGatewayLocalisation("US"))
                .toString()
                .toByteArray()
        }
        return nodeRepository.createCardinityPaymentGatewayOrder(req)
    }

    suspend fun paymentOrderCallback(
        action: (OrderUpdatedCallbackPayload) -> Unit
    ) = nodeRepository.registerOrderUpdatedCallback {
        action.invoke(it)
    }

    suspend fun getGateways() = nodeRepository.getGateways()
}
