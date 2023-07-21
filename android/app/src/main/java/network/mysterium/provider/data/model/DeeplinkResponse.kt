package network.mysterium.provider.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeeplinkResponse(
    @SerialName("link")
    val link: String,
)
