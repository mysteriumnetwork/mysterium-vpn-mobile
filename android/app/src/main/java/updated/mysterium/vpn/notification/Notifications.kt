package updated.mysterium.vpn.notification

import android.app.Activity
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.pushy.sdk.Pushy
import updated.mysterium.vpn.common.extensions.getPushySubcategoryName
import updated.mysterium.vpn.common.extensions.getSubcategoryName
import updated.mysterium.vpn.model.pushy.PushyTopic

class Notifications(private val activity: Activity) {

    companion object {
        const val PERMISSION_REQUEST_EXT_STORAGE = 0
        private const val TAG = "Notifications"
    }

    var deviceToken: String? = null
        private set

    fun listen() {
        if (Pushy.isRegistered(activity.applicationContext)) {
            Pushy.listen(activity.applicationContext)
        }
    }

    fun register(onRegisteredAction: () -> Unit) {
        if (!Pushy.isRegistered(activity.applicationContext)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                Log.e(TAG, "Failed to register to pushy.me", exception)
            }
            CoroutineScope(Dispatchers.IO).launch(handler) {
                Pushy.register(activity.applicationContext)
                deviceToken = Pushy.getDeviceCredentials(activity.applicationContext).token
                Log.i(TAG, Pushy.getDeviceCredentials(activity.applicationContext).token)
                onRegisteredAction.invoke()
            }
        } else {
            deviceToken = Pushy.getDeviceCredentials(activity.applicationContext).token
            onRegisteredAction.invoke()
        }
    }

    fun subscribe(pushyTopic: String) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Failed to subscribe", exception)
        }
        CoroutineScope(Dispatchers.IO).launch(handler) {
            Pushy.subscribe(pushyTopic.getPushySubcategoryName(), activity.applicationContext)
            // Unsubscribe from same topic without subcategory
            Pushy.unsubscribe(pushyTopic, activity.applicationContext)
        }
    }

    fun subscribe(pushyTopic: PushyTopic) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Failed to subscribe", exception)
        }
        CoroutineScope(Dispatchers.IO).launch(handler) {
            Pushy.subscribe(pushyTopic.getSubcategoryName(), activity.applicationContext)
            // Unsubscribe from same topic without subcategory
            Pushy.unsubscribe(pushyTopic.topic, activity.applicationContext)
        }
    }

    fun unsubscribe(pushyTopic: PushyTopic) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Failed to unsubscribe", exception)
        }
        CoroutineScope(Dispatchers.IO).launch(handler) {
//             Unsubscribe from topic with and without subcategory
            Pushy.unsubscribe(pushyTopic.getSubcategoryName(), activity.applicationContext)
            Pushy.unsubscribe(pushyTopic.topic, activity.applicationContext)
        }
    }
}
