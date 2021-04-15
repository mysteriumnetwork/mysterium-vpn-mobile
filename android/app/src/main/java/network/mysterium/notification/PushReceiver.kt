package network.mysterium.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import me.pushy.sdk.Pushy
import network.mysterium.vpn.R
import updated.mysterium.vpn.ui.wallet.WalletActivity

class PushReceiver : BroadcastReceiver() {

    companion object {
        const val PUSHY_BALANCE_ACTION = "android.intent.action.BALANCE_RUNNING_OUT"
        const val NOTIFICATION_TITLE = "title"
        const val NOTIFICATION_MESSAGE = "message"
        const val NOTIFICATION_URL = "url"
        private const val PUSHY_NOTIFICATION_ID = 1
        private const val BALANCE_NOTIFICATION_ID = 2
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == PUSHY_BALANCE_ACTION) {
            showBalancePush(context, intent)
        } else {
            showMarketingPush(context, intent)
        }
    }

    private fun showBalancePush(context: Context, intent: Intent) {
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

        val resultIntent = Intent(context, WalletActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, FLAG_UPDATE_CURRENT)
        }
        builder.setContentIntent(resultPendingIntent)

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(BALANCE_NOTIFICATION_ID, builder.build())
    }

    private fun showMarketingPush(context: Context, intent: Intent) {
        val title = intent.getStringExtra(NOTIFICATION_TITLE) ?: "MysteriumVPN"
        val message = intent.getStringExtra(NOTIFICATION_MESSAGE) ?: ""
        val url = intent.getStringExtra(NOTIFICATION_URL)

        val builder = NotificationCompat.Builder(context, "")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setVibrate(longArrayOf(0, 400, 250, 400))
            .setSound(RingtoneManager.getDefaultUri(TYPE_NOTIFICATION))

        Pushy.setNotificationChannel(builder, context)

        url?.let {
            val notificationIntent = Intent(Intent.ACTION_VIEW)
            notificationIntent.data = Uri.parse(it)
            val pending = PendingIntent.getActivity(context, 0, notificationIntent, FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pending)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(PUSHY_NOTIFICATION_ID, builder.build())
    }
}
