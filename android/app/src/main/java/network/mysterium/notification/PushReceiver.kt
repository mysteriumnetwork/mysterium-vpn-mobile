package network.mysterium.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.media.RingtoneManager.TYPE_NOTIFICATION
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import me.pushy.sdk.Pushy
import network.mysterium.vpn.R
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.wallet.WalletActivity

class PushReceiver : BroadcastReceiver() {

    companion object {
        const val PUSHY_BALANCE_ACTION = "android.intent.action.BALANCE_RUNNING_OUT"
        const val PUSHY_CONNECTION_ACTION = "android.intent.action.PUSHY_CONNECTION_ACTION"
        const val NOTIFICATION_TITLE = "title"
        const val NOTIFICATION_MESSAGE = "message"
        private const val PUSHY_NOTIFICATION_ID = 1
        private const val BALANCE_NOTIFICATION_ID = 2
        private const val CONNECTION_NOTIFICATION_ID = 3
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            PUSHY_BALANCE_ACTION -> {
                showBalancePush(context, intent)
            }
            PUSHY_CONNECTION_ACTION -> {
                showConnectionPush(context, intent)
            }
            else -> {
                showMarketingPush(context, intent)
            }
        }
    }

    private fun showBalancePush(context: Context, intent: Intent) {
        val builder = createNotification(intent, context)
        val resultIntent = Intent(context, WalletActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, FLAG_UPDATE_CURRENT)
        }
        builder.setContentIntent(resultPendingIntent)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(BALANCE_NOTIFICATION_ID, builder.build())
    }

    private fun showConnectionPush(context: Context, intent: Intent) {
        val builder = createNotification(intent, context)
        val resultIntent = Intent(context, HomeActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, FLAG_UPDATE_CURRENT)
        }
        builder.setContentIntent(resultPendingIntent)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(CONNECTION_NOTIFICATION_ID, builder.build())
    }

    private fun showMarketingPush(context: Context, intent: Intent) {
        val builder = createNotification(intent, context)
        val resultIntent = Intent(context, HomeActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, FLAG_UPDATE_CURRENT)
        }
        builder.setContentIntent(resultPendingIntent)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(PUSHY_NOTIFICATION_ID, builder.build())
    }

    private fun createNotification(intent: Intent, context: Context): NotificationCompat.Builder {
        val title = intent.getStringExtra(NOTIFICATION_TITLE) ?: "MysteriumVPN"
        val message = intent.getStringExtra(NOTIFICATION_MESSAGE) ?: ""
        val builder = NotificationCompat.Builder(context, "")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setVibrate(longArrayOf(0, 400, 250, 400))
            .setSound(RingtoneManager.getDefaultUri(TYPE_NOTIFICATION))

        Pushy.setNotificationChannel(builder, context)

        return builder
    }
}
