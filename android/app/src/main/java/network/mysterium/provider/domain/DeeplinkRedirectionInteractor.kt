package network.mysterium.provider.domain

import network.mysterium.provider.data.datasource.DeeplinkDataSource
import network.mysterium.provider.domain.model.Deeplink

interface DeeplinkRedirectionInteractor {

    suspend fun getDeeplinkRedirection(url: String): Deeplink

}

class DeeplinkRedirectionInteractorImpl(private val deeplinkDataSource: DeeplinkDataSource) :
    DeeplinkRedirectionInteractor {

    override suspend fun getDeeplinkRedirection(url: String): Deeplink =
        deeplinkDataSource.getDeeplinkRedirection(url)
}
