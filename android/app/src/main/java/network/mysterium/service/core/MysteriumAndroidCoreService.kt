/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package network.mysterium.service.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.MainActivity
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R

class MysteriumAndroidCoreService : VpnService() {
    private var mobileNode: MobileNode? = null
    private val notificationsChannelId = BuildConfig.APPLICATION_ID

    // pendingAppIntent is used to navigate back to MainActivity
    // when user taps on notification.
    private lateinit var pendingAppIntent: PendingIntent

    fun startMobileNode(filesPath: String): MobileNode {
        if (mobileNode != null) {
            return mobileNode!!
        }

        val openvpnBridge = Openvpn3AndroidTunnelSetupBridge(this)
        val wireguardBridge = WireguardAndroidTunnelSetup(this)

        val logOptions = Mysterium.defaultLogOptions()
        logOptions.filepath = filesPath
        logOptions.logHTTP = false
        val options = Mysterium.defaultNetworkOptions()

        mobileNode = Mysterium.newNode(filesPath, logOptions, options)
        mobileNode?.overrideOpenvpnConnection(openvpnBridge)
        mobileNode?.overrideWireguardConnection(wireguardBridge)

        Log.i(TAG, "started")
        return mobileNode!!
    }

    fun stopMobileNode() {
        val node = mobileNode
        if (node == null) {
            Log.w(TAG, "Trying to stop node when instance is not set")
            return
        }

        node.shutdown()
        try {
            node.waitUntilDies()
        } catch (e: Exception) {
            Log.i(TAG, "Got exception, safe to ignore: " + e.message)
        } finally {
            stopForeground(true)
        }
    }

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        pendingAppIntent = PendingIntent.getActivity(this, 0, intent, 0)

        createNotificationChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMobileNode()
        // TODO: Check if node is destroyed correctly.
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(notificationsChannelId, notificationsChannelId, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableVibration(false)
        notificationManager.createNotificationChannel(channel)
    }

    // startForeground starts service with given notifications in foreground.
    fun startForeground(title: String, content: String = "") {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }
        val notification = NotificationCompat.Builder(this, notificationsChannelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(LongArray(0))
                .setContentIntent(pendingAppIntent)
                .setOnlyAlertOnce(true)

        if (content != "") {
            notification.setContentText(content)
        }

        startForeground(1, notification.build())
    }

    fun stopForeground() {
        stopForeground(true)
    }

    override fun onRevoke() {
        Log.w(TAG, "VPN service revoked!")
    }

    inner class MysteriumCoreServiceBridge : Binder(), MysteriumCoreService {
        override fun startNode(): MobileNode {
            return startMobileNode(filesDir.canonicalPath)
        }

        override fun stopNode() {
            stopMobileNode()
        }

        override fun showNotification(title: String, content: String) {
            startForeground(title, content)
        }

        override fun hideNotifications() {
            stopForeground()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return MysteriumCoreServiceBridge()
    }

    companion object {
        private const val TAG = "MysteriumVPNService"
    }
}
