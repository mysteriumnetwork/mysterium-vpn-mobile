package network.mysterium

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import updated.mysterium.vpn.ui.manual.connect.home.HomeViewModel

@KoinApiExtension
class AppBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val viewModel: HomeViewModel by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AppNotificationManager.ACTION_DISCONNECT) {
            handleDisconnect()
        }
    }

    private fun handleDisconnect() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.disconnect()
        }
    }
}
