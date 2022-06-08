package updated.mysterium.vpn.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import updated.mysterium.vpn.common.extensions.TAG

class ReviveUserWork(context: Context, params: WorkerParameters) : Worker(context, params), KoinComponent {

    companion object {
        const val WEEK_DELAY_NOTIFICATION = "WEEK_DELAY_NOTIFICATION"
        const val TWO_WEEKS_DELAY_NOTIFICATION = "TWO_WEEKS_DELAY_NOTIFICATION"
        const val MONTH_DELAY_NOTIFICATION = "MONTH_DELAY_NOTIFICATION"
    }

    private val appNotificationManager: AppNotificationManager by inject()

    override fun doWork(): Result {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            Result.failure()
        }
        CoroutineScope(Dispatchers.Main + handler).launch {
            appNotificationManager.showInactiveUserNotification()
        }
        return Result.success()
    }

}
