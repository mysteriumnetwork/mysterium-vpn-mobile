package updated.mysterium.vpn.analytics

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import updated.mysterium.vpn.analytics.mysterium.MysteriumAnalyticService
import updated.mysterium.vpn.model.analytics.ClientAnalyticRequest
import updated.mysterium.vpn.model.analytics.EventAnalyticRequest

class AnalyticWrapper {

    private companion object {
        const val SUCCESS_TRACK_CODE = 202
        const val TAG = "AnalyticWrapper"
    }

    private var apiInterface: MysteriumAnalyticService? = null

    fun trackEvent(event: ClientAnalyticRequest, retry: Boolean = false) {
        createApiInstance()
        Log.i(TAG, event.toString())
        val call = apiInterface?.trackEvent(event)
        call?.enqueue(
            object : Callback<Unit?> {

                override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                    if (response.code() != SUCCESS_TRACK_CODE && retry.not()) {
                        trackEvent(event, true)
                    }
                }

                override fun onFailure(call: Call<Unit?>, throwable: Throwable) {
                    Log.i(TAG, throwable.localizedMessage ?: throwable.toString())
                    if (retry.not()) {
                        trackEvent(event, true)
                    }
                }
            }
        )
    }

    fun trackEvent(event: EventAnalyticRequest, retry: Boolean = false) {
        createApiInstance()
        Log.i(TAG, event.toString())
        val call = apiInterface?.trackEvent(event)
        call?.enqueue(
            object : Callback<Unit?> {

                override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                    if (response.code() != SUCCESS_TRACK_CODE && retry.not()) {
                        trackEvent(event, true)
                    }
                }

                override fun onFailure(call: Call<Unit?>, throwable: Throwable) {
                    Log.i(TAG, throwable.localizedMessage ?: throwable.toString())
                    if (retry.not()) {
                        trackEvent(event, true)
                    }
                }
            }
        )
    }

    private fun createApiInstance(): MysteriumAnalyticService? {
        if (apiInterface != null) {
            return apiInterface
        }
        apiInterface = MysteriumAnalyticService.create()
        return apiInterface
    }
}
