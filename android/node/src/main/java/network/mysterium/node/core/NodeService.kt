package network.mysterium.node.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import mysterium.MobileNode
import mysterium.Mysterium
import network.mystrium.node.R

class NodeService : Service() {

    companion object {
        const val CHANNEL_ID = "mystnodes.channel"
        const val NOTIFICATION_ID = 1
    }

    private var mobileNode: MobileNode? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return Bridge()
    }

    private fun startForegroundNotification() {
        val intent =
            packageManager.getLaunchIntentForPackage("network.mysterium.provider") ?: return
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Connected")
            .setColor(Color.BLACK)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Myst Nodes",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    internal inner class Bridge : Binder(), NodeServiceBinder {

        override val node: MobileNode?
            get() = mobileNode

        override fun start() {
            if (mobileNode != null) {
                return
            }
            val node = Mysterium.newNode(
                filesDir.canonicalPath,
                Mysterium.defaultProviderNodeOptions()
            )
            mobileNode = node
        }

        override fun startForeground() {
            startForegroundNotification()
        }

        override fun stop() {
        }
    }
}