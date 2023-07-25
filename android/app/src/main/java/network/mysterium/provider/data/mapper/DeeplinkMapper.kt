package network.mysterium.provider.data.mapper

import network.mysterium.provider.data.model.DeeplinkResponse
import network.mysterium.provider.domain.model.Deeplink

fun DeeplinkResponse.toDomain() = Deeplink(this.link)
