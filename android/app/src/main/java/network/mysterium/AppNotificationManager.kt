package network.mysterium

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CompletableDeferred
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.vpn.R

typealias NotificationFactory = (Context) -> Notification

class AppNotificationManager(
        private val notificationManager: NotificationManager,
        private val mysteriumCoreService: CompletableDeferred<MysteriumCoreService>
) {
    private val statisticsChannel = "statistics"
    private val connLostChannel = "connectionlost"
    private val topUpBalanceChannel = "topupbalance"

    val defaultNotificationID = 1
    private val topupBalanceNotificationID = 2

    // pendingAppIntent is used to navigate back to MainActivity
    // when user taps on notification.
    private lateinit var pendingAppIntent: PendingIntent

    fun init(ctx: Context) {
        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        pendingAppIntent = PendingIntent.getActivity(ctx, 0, intent, 0)

        createChannel(statisticsChannel)
        createChannel(connLostChannel)
        createChannel(topUpBalanceChannel)
    }

    private fun createChannel(channelId: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableVibration(false)
        notificationManager.createNotificationChannel(channel)
    }

    fun createConnectedToVPNNotification(): NotificationFactory {
        return {
            NotificationCompat.Builder(it, statisticsChannel)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Connected")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVibrate(LongArray(0))
                    .setContentIntent(pendingAppIntent)
                    .setOnlyAlertOnce(true)
                    .build()
        }
    }

    suspend fun showStatisticsNotification(title: String, content: String) {
        val ctx = mysteriumCoreService.await().getContext()
        val disconnectIntent = Intent(ctx, AppBroadcastReceiver::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent: PendingIntent = PendingIntent.getBroadcast(ctx, 0, disconnectIntent, 0)

        val notification = NotificationCompat.Builder(ctx, statisticsChannel)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(LongArray(0))
                .setContentIntent(pendingAppIntent)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_close_black_24dp, "Disconnect", disconnectPendingIntent)
                .build()
        notificationManager.notify(defaultNotificationID, notification)
    }

    suspend fun showConnectionLostNotification() {
        val ctx = mysteriumCoreService.await().getContext()
        val notification = NotificationCompat.Builder(ctx, connLostChannel)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Connection lost")
                .setContentText("VPN connection was closed.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(LongArray(0))
                .setContentIntent(pendingAppIntent)
                .setOnlyAlertOnce(true)
                .build()
        notificationManager.notify(defaultNotificationID, notification)
    }

    suspend fun showTopUpBalanceNotification() {
        val ctx = mysteriumCoreService.await().getContext()
        val notification = NotificationCompat.Builder(ctx, topUpBalanceChannel)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Top-up balance")
                .setContentText("You need to top-up your balance to continue using VPN service.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(LongArray(0))
                .setContentIntent(pendingAppIntent)
                .setOnlyAlertOnce(true)
                .build()
        notificationManager.notify(topupBalanceNotificationID, notification)
    }

    companion object {
        const val ACTION_DISCONNECT = "DISCONNECT"
    }
}
