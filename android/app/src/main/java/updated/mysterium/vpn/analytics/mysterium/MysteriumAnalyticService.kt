package updated.mysterium.vpn.analytics.mysterium

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import updated.mysterium.vpn.model.analytics.ClientAnalyticRequest
import updated.mysterium.vpn.model.analytics.EventAnalyticRequest

interface MysteriumAnalyticService {

    companion object {

        val analyticService: MysteriumAnalyticService by lazy {
            Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MysteriumAnalyticService::class.java)
        }

        private const val BASE_URL = "https://consumetrics.mysterium.network/api/v1/"
    }

    @POST("events")
    fun trackEvent(@Body event: EventAnalyticRequest): Call<Unit?>?

    @POST("events")
    fun trackEvent(@Body event: ClientAnalyticRequest): Call<Unit?>?
}
