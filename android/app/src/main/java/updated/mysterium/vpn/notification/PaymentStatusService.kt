package updated.mysterium.vpn.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class PaymentStatusService : Service() {

    private companion object {
        val TAG: String = PaymentStatusService::class.java.simpleName
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    // stopSelf()
    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
    }
}
