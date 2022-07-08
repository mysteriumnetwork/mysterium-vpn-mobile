package updated.mysterium.vpn.model.nodes

import com.squareup.moshi.Json

class ProposalItem(
    @Json(name = "provider_id")
    val providerID: String,

    @Json(name = "service_type")
    val serviceType: String,

    @Json(name = "country")
    val countryCode: String,

    @Json(name = "ip_type")
    val nodeType: String = "",

    @Json(name = "quality_level")
    val qualityLevel: Int,

    @Json(name = "price")
    val payment: ProposalPaymentMethod
)
