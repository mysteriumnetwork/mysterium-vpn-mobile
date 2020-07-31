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
import me.pushy.sdk.Pushy
import network.mysterium.vpn.R

class PushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "MysteriumVPN"
        val message = intent.getStringExtra("message") ?: ""
        val url = intent.getStringExtra("url")

        // Prepare a notification with vibration, sound and lights
        val builder = NotificationCompat.Builder(context, "")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(longArrayOf(0, 400, 250, 400))
                .setSound(RingtoneManager.getDefaultUri(TYPE_NOTIFICATION))
        // Automatically configure a Notification Channel for devices running Android O+
        Pushy.setNotificationChannel(builder, context)

        url?.let {
            val notificationIntent = Intent(Intent.ACTION_VIEW)
            notificationIntent.data = Uri.parse(it)
            val pending = PendingIntent.getActivity(context, 0, notificationIntent, FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pending)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, builder.build())
    }
}
