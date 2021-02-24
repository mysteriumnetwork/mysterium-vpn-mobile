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
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

@KoinApiExtension
class AppBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val useCaseProvider: UseCaseProvider by inject()
    private val connectionUseCase = useCaseProvider.connection()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AppNotificationManager.ACTION_DISCONNECT) {
            handleDisconnect()
        }
    }

    private fun handleDisconnect() {
        CoroutineScope(Dispatchers.Main).launch {
            connectionUseCase.disconnect()
        }
    }
}
