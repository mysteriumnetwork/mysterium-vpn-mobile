package updated.mysterium.vpn.analytics

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import updated.mysterium.vpn.analytics.mysterium.*
import updated.mysterium.vpn.model.analytics.ClientInfo
import updated.mysterium.vpn.model.analytics.EventInfo

class AnalyticWrapper(private val mysteriumAnalyticBuilder: MysteriumAnalyticBuilder) {

    private companion object {
        const val SUCCESS_TRACK_CODE = 202
        const val TAG = "AnalyticWrapper"
    }

    private var apiInterface: MysteriumAnalyticService? = null

    fun trackClient(analyticEvent: AnalyticEvent) {
        val clientInfo = mysteriumAnalyticBuilder.getClientInfo(analyticEvent.eventName)
        handleTracking(clientInfo)
    }

    fun trackEvent(analyticEvent: ClientInfo, pageTitle: String? = null) {
        val eventInfo = mysteriumAnalyticBuilder.getEventInfo(
            analyticEvent.eventName, pageTitle
        )
        handleTracking(eventInfo)
    }

    private fun handleTracking(event: ClientInfo, retry: Boolean = false) {
        createApiInstance()
        Log.i(TAG, event.toString())
        val call = apiInterface?.trackEvent(event)
        call?.enqueue(
            object : Callback<Unit?> {

                override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                    Log.i(TAG, response.code().toString())
                    if (response.code() != SUCCESS_TRACK_CODE && retry.not()) {
                        handleTracking(event, true)
                    }
                }

                override fun onFailure(call: Call<Unit?>, throwable: Throwable) {
                    Log.i(TAG, throwable.localizedMessage ?: throwable.toString())
                    if (retry.not()) {
                        handleTracking(event, true)
                    }
                }
            }
        )
    }

    private fun handleTracking(event: EventInfo, retry: Boolean = false) {
        createApiInstance()
        val call = apiInterface?.trackEvent(event)
        call?.enqueue(
            object : Callback<Unit?> {

                override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                    Log.i(TAG, response.code().toString())
                    if (response.code() != SUCCESS_TRACK_CODE && retry.not()) {
                        handleTracking(event, true)
                    }
                }

                override fun onFailure(call: Call<Unit?>, throwable: Throwable) {
                    Log.i(TAG, throwable.localizedMessage ?: throwable.toString())
                    if (retry.not()) {
                        handleTracking(event, true)
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
