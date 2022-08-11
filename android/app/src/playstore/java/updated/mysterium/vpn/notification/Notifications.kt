package updated.mysterium.vpn.notification

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.pushy.sdk.Pushy
import updated.mysterium.vpn.common.extensions.getPushySubcategoryName
import updated.mysterium.vpn.common.extensions.getSubcategoryName
import updated.mysterium.vpn.common.playstore.NotificationsHelper
import updated.mysterium.vpn.model.pushy.PushyTopic

class Notifications(private val context: Context): NotificationsHelper {

    companion object {
        private const val TAG = "Notifications"
    }

    var deviceToken: String? = null
        private set

    override fun listen() {
        if (Pushy.isRegistered(context)) {
            Pushy.listen(context)
        }
    }

    override fun register(onRegisteredAction: () -> Unit) {
        if (!Pushy.isRegistered(context)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                Log.e(TAG, "Failed to register to pushy.me", exception)
            }
            CoroutineScope(Dispatchers.IO).launch(handler) {
                Pushy.register(context)
                deviceToken = Pushy.getDeviceCredentials(context).token
                Log.i(TAG, Pushy.getDeviceCredentials(context).token)
                onRegisteredAction.invoke()
            }
        } else {
            deviceToken = Pushy.getDeviceCredentials(context).token
            onRegisteredAction.invoke()
        }
    }

    override fun subscribe(pushyTopic: String) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Failed to subscribe", exception)
        }
        CoroutineScope(Dispatchers.IO).launch(handler) {
            Pushy.subscribe(pushyTopic.getPushySubcategoryName(), context)
            // Unsubscribe from same topic without subcategory
            Pushy.unsubscribe(pushyTopic, context)
        }
    }

    override fun subscribe(pushyTopic: PushyTopic) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Failed to subscribe", exception)
        }
        CoroutineScope(Dispatchers.IO).launch(handler) {
            Pushy.subscribe(pushyTopic.getSubcategoryName(), context)
            // Unsubscribe from same topic without subcategory
            Pushy.unsubscribe(pushyTopic.topic, context)
        }
    }

    override fun unsubscribe(pushyTopic: PushyTopic) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Failed to unsubscribe", exception)
        }
        CoroutineScope(Dispatchers.IO).launch(handler) {
            // Unsubscribe from topic with and without subcategory
            Pushy.unsubscribe(pushyTopic.getSubcategoryName(), context)
            Pushy.unsubscribe(pushyTopic.topic, context)
        }
    }
}
