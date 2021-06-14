package updated.mysterium.vpn.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import updated.mysterium.vpn.ui.connection.ConnectionViewModel

class AppBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private companion object {
        const val TAG = "AppBroadcastReceiver"
    }

    private val viewModel: ConnectionViewModel by inject()
    private val appNotificationManager: AppNotificationManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AppNotificationManager.ACTION_DISCONNECT) {
            handleDisconnect()
        }
    }

    private fun handleDisconnect() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        CoroutineScope(Dispatchers.Main + handler).launch {
            appNotificationManager.hideStatisticsNotification()
            viewModel.disconnectFromNotification()
        }
    }
}
