package updated.mysterium.vpn.analytics

import io.intercom.android.sdk.models.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface MysteriumAnalyticService {

    companion object {
        private const val BASE_URL = "https://consumetrics.mysterium.network/api/v1/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @POST("{event}")
    fun trackEvent(
        @Path("event") eventName: String,
        @Body analyticBody: MysteriumAnalyticBody
    ): Call<Unit?>?
}
