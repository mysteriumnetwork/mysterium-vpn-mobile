package updated.mysterium.vpn.analitics

import android.content.Context
import android.util.Log
import org.matomo.sdk.Matomo
import org.matomo.sdk.TrackMe
import org.matomo.sdk.Tracker
import org.matomo.sdk.TrackerBuilder
import org.matomo.sdk.extra.DimensionQueue
import org.matomo.sdk.extra.TrackHelper

class AnalyticWrapper(private val context: Context) {

    private companion object {
        private const val TAG = "MatomoApp"
        private const val MATOMO_INSTANCE = "https://analytics.mysterium.network/matomo.php"
        private const val MATOMO_INSTANCE_BASE = "https://analytics.mysterium.network/"
        private const val MATOMO_SITE_ID = 4
    }

    private val tracker: Tracker? by lazy {
        TrackerBuilder
            .createDefault(MATOMO_INSTANCE, MATOMO_SITE_ID)
            .setApplicationBaseUrl(MATOMO_INSTANCE_BASE)
            .build(Matomo.getInstance(context))
    }

    init {
        tracker?.addTrackingCallback { trackMe: TrackMe? ->
            Log.i(TAG, trackMe?.toMap().toString())
            trackMe
        }
        DimensionQueue(tracker).apply {
            add(MATOMO_SITE_ID, TAG)
        }
    }

    fun track(event: AnalyticEvent) {
        when (event) {
            AnalyticEvent.LOGIN -> loginEvent()
            AnalyticEvent.NEW_SESSION -> newSessionEvent()
            else -> Log.e(TAG, "Wrong event trying to sent")
        }
    }

    fun track(event: AnalyticEvent, floatValue: Float) {
        if (event == AnalyticEvent.VPN_TIME) {
            vpnTimeEvent(floatValue)
        }
    }

    fun track(event: AnalyticEvent, stringValue: String) {
        if (event == AnalyticEvent.COUNTRY_SELECTED) {
            vpnCountrySelected(stringValue)
        } else if (event == AnalyticEvent.NEW_PUSHY_DEVICE) {
            newPushyDeviceEvent(stringValue)
        }
    }

    fun track(event: AnalyticEvent, stringValue: String, floatValue: Float) {
        if (event == AnalyticEvent.PAYMENT) {
            paymentEvent(stringValue, floatValue)
        } else if (event == AnalyticEvent.REFERRAL_TOKEN) {
            referralTokenEvent(stringValue, floatValue)
        }
    }

    private fun loginEvent() {
        TrackHelper.track()
            .event("User Action", "App started")
            .name("New visit")
            .with(tracker)
    }

    private fun vpnTimeEvent(value: Float? = null) {
        val eventBuilder = TrackHelper.track()
            .event("Time using VPN", "Session duration, min")
            .name("Connection duration $value min")
        value?.let {
            eventBuilder.value(it)
        }
        eventBuilder.with(tracker)
    }

    private fun newSessionEvent() {
        TrackHelper.track()
            .event("User action", "All VPN sessions count")
            .name("New session started")
            .with(tracker)
    }

    private fun vpnCountrySelected(countryName: String) {
        TrackHelper.track()
            .event("VPN Countries", countryName)
            .name("$countryName selected")
            .with(tracker)
    }

    private fun paymentEvent(currency: String, amount: Float) {
        TrackHelper.track()
            .event("Payment amount", currency)
            .name("$currency payment $amount")
            .value(amount)
            .with(tracker)
    }

    private fun referralTokenEvent(token: String, amount: Float) {
        TrackHelper.track()
            .event("Referral token", token)
            .name("$token costs $amount")
            .value(amount)
            .with(tracker)
    }

    private fun newPushyDeviceEvent(pushyId: String) {
        TrackHelper.track()
            .event("New Pushy ID", pushyId)
            .name("User registered with $pushyId ID")
            .with(tracker)
    }
}
