package updated.mysterium.vpn.notification

import android.app.Activity
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.pushy.sdk.Pushy

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

    fun register() {
        if (!Pushy.isRegistered(activity.applicationContext)) {
            val handler = CoroutineExceptionHandler { _, exception ->
                Log.e(TAG, "Failed to register to pushy.me", exception)
            }
            CoroutineScope(Dispatchers.IO).launch(handler) {
                Pushy.register(activity.applicationContext)
                deviceToken = Pushy.getDeviceCredentials(activity.applicationContext).token
                Log.i(TAG, Pushy.getDeviceCredentials(activity.applicationContext).token)
            }
        } else {
            deviceToken = Pushy.getDeviceCredentials(activity.applicationContext).token
        }
    }
}