package network.mysterium.notification

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.pushy.sdk.Pushy
import me.pushy.sdk.util.exceptions.PushyException

const val PERMISSION_REQUEST_EXT_STORAGE = 0

class Notifications(val activity: Activity) : ActivityCompat.OnRequestPermissionsResultCallback {

    fun listen() {
        if (Pushy.isRegistered(activity.applicationContext)) {
            Log.i(TAG, "Pushy: listening")
            Pushy.listen(activity.applicationContext)
        }
    }

    fun registerOrRequestPermissions() {
        // Request WRITE_EXTERNAL_STORAGE for pushy.me to read/store the device token
        if (ContextCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage
            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage
            requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_EXT_STORAGE)
        } else {
            register()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_EXT_STORAGE) {
            Log.i(TAG, "EXTERNAL_STORAGE permission results $grantResults")
            // Register anyway
            register()
        }
    }

    private fun register() {
        if (!Pushy.isRegistered(activity.applicationContext)) {
            CoroutineScope(Dispatchers.IO).launch {
                // We may send device token to the backend here for storing
                try {
                    Pushy.register(activity.applicationContext)
                } catch (e: PushyException) {
                    Log.e(TAG, "Failed to register to pushy.me", e)
                }
            }
        } else {
            val token = Pushy.getDeviceCredentials(activity.applicationContext).token
            Log.i(TAG, "Pushy.me device token: $token")
        }
    }

    companion object {
        private const val TAG = "Notifications"
    }

}
