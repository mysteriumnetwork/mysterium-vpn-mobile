package network.mysterium.provider.data.datasource

import network.mysterium.provider.Config
import network.mysterium.provider.data.api.DeeplinkApi
import network.mysterium.provider.data.mapper.toDomain
import network.mysterium.provider.domain.model.Deeplink


private const val REDIRECT_URI = "redirect_uri="

interface DeeplinkDataSource {

    suspend fun getDeeplinkRedirection(url: String): Deeplink

}

class DeeplinkDataSourceImpl(private val deeplinkApi: DeeplinkApi) : DeeplinkDataSource {

    override suspend fun getDeeplinkRedirection(url: String): Deeplink =
        deeplinkApi.mapDeeplink(url.toDeeplinkScheme()).toDomain()

    private fun String.toDeeplinkScheme(): String =
        this.replaceAfter(REDIRECT_URI, Config.deeplinkSSO.run { host + scheme })

}
