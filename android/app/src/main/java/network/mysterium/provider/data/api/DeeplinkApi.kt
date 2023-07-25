package network.mysterium.provider.data.api

import network.mysterium.provider.data.model.DeeplinkResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface DeeplinkApi {

    @GET
    suspend fun mapDeeplink(@Url url: String): DeeplinkResponse

}
