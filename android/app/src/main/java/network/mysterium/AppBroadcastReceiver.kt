package network.mysterium

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == AppNotificationManager.ACTION_DISCONNECT) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val appContainer = (context.applicationContext as MainApplication).appContainer
                    appContainer.sharedViewModel.disconnect()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to disconnect")
                }
            }
        }

        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.i(TAG, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val TAG = "AppBroadcastReceiver"
    }
}