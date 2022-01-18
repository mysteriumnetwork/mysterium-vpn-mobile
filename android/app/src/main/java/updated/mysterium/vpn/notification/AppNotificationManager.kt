package updated.mysterium.vpn.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import network.mysterium.vpn.R
import updated.mysterium.vpn.model.notification.NotificationChannels
import updated.mysterium.vpn.ui.connection.ConnectionActivity
import updated.mysterium.vpn.ui.splash.SplashActivity

typealias NotificationFactory = (Context) -> Notification

class AppNotificationManager(private val notificationManager: NotificationManager) {

    companion object {
        const val ACTION_DISCONNECT = "DISCONNECT"
    }

    private val statisticsChannel = "statistics"
    private val connLostChannel = "connectionlost"
    private val topUpBalanceChannel = "topupbalance"
    private val paymentStatusChannel = "paymentstatus"
    private lateinit var context: Context

    // pendingAppIntent is used to navigate back to MainActivity
    // when user taps on notification.
    private lateinit var pendingAppIntent: PendingIntent

    fun init(ctx: Context) {
        context = ctx
        val intent = Intent(ctx, ConnectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        pendingAppIntent = PendingIntent.getActivity(ctx, 0, intent, 0)

        createChannel(statisticsChannel)
        createChannel(connLostChannel)
        createChannel(topUpBalanceChannel)
        createChannel(paymentStatusChannel)
    }

    private fun createChannel(channelId: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableVibration(false)
        notificationManager.createNotificationChannel(channel)
    }

    fun createConnectedToVPNNotification(): NotificationFactory {
        return {
            NotificationCompat.Builder(it, statisticsChannel)
                .setSmallIcon(R.drawable.notification_logo)
                .setContentTitle("Connected")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(LongArray(0))
                .setContentIntent(pendingAppIntent)
                .setOnlyAlertOnce(true)
                .build()
        }
    }

    fun showStatisticsNotification(title: String, content: String) {
        val disconnectIntent = Intent(context, AppBroadcastReceiver::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getBroadcast(
            context, 0, disconnectIntent, 0
        )

        val notification = NotificationCompat.Builder(context, statisticsChannel)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingAppIntent)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.icon_close_black, "Disconnect", disconnectPendingIntent)
            .build()
        val statisticNotification = notificationManager.activeNotifications.find {
            it.id == NotificationChannels.STATISTIC_NOTIFICATION
        }
        if (statisticNotification != null) {
            notificationManager.notify(NotificationChannels.STATISTIC_NOTIFICATION, notification)
        }
    }

    fun hideStatisticsNotification() {
        notificationManager.cancel(NotificationChannels.STATISTIC_NOTIFICATION)
    }

    fun showDownloadedNotification() {
        val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val notification = NotificationCompat.Builder(context, connLostChannel)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle("MysteriumKeystore")
            .setContentText("File downloaded to Download folder")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setVibrate(LongArray(0))
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NotificationChannels.PRIVATE_KEY_NOTIFICATION_ID, notification)
    }

    fun showSuccessPaymentNotification(amount: String, currency: String) {
        val intent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val appIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, paymentStatusChannel)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(context.getString(R.string.push_notification_payment_success_title))
            .setContentText(
                context.getString(
                    R.string.push_notification_payment_success_message,
                    amount,
                    currency
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(LongArray(0))
            .setContentIntent(appIntent)
            .setOnlyAlertOnce(true)
            .build()

        notificationManager.notify(NotificationChannels.STATISTIC_NOTIFICATION, notification)
    }

    fun showFailedPaymentNotification() {
        val intent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val appIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, paymentStatusChannel)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(context.getString(R.string.push_notification_payment_failed_title))
            .setContentText(context.getString(R.string.push_notification_payment_failed_message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(LongArray(0))
            .setContentIntent(appIntent)
            .setOnlyAlertOnce(true)
            .build()

        notificationManager.notify(NotificationChannels.STATISTIC_NOTIFICATION, notification)
    }
}
