package network.mysterium.node.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BatteryStatus(private val context: Context) {

    val isCharging: StateFlow<Boolean>
        get() = isChargingFlow.asStateFlow()

    private val isChargingFlow = MutableStateFlow(true)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isChargingFlow.update { intent.action == Intent.ACTION_POWER_CONNECTED }
        }
    }

    init {
        setInitialChargingState()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        context.registerReceiver(receiver, filter)
    }

    private fun setInitialChargingState() {
        val batteryStatusIntent =
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        isChargingFlow.update {
            batteryStatusIntent?.getIntExtra(
                BatteryManager.EXTRA_STATUS,
                -1
            ).let { batteryStatus ->
                batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                        batteryStatus == BatteryManager.BATTERY_STATUS_FULL
            }
        }
    }
}
