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
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mysterium.MobileNode
import network.mysterium.node.Storage
import network.mysterium.node.battery.BatteryStatus
import network.mysterium.node.data.NodeServiceDataSource
import network.mysterium.node.extensions.isFirstDayOfMonth
import network.mysterium.node.extensions.nextDay
import network.mysterium.node.model.NodeUsage
import network.mysterium.node.network.NetworkReporter
import network.mysterium.node.network.NetworkType
import network.mystrium.node.R
import org.koin.android.ext.android.inject
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.timer

class NodeService : Service() {

    private companion object {
        const val CHANNEL_ID = "mystnodes.channel"
        const val NOTIFICATION_ID = 1
        val BALANCE_CHECK_INTERVAL = TimeUnit.MINUTES.toMillis(1)
        val TAG: String = NodeService::class.java.simpleName
    }

    private var isStarted: Boolean = false

    private val mobileNode by inject<MobileNode>()
    private val nodeServiceDataSource by inject<NodeServiceDataSource>()
    private val networkReporter by inject<NetworkReporter>()
    private val storage by inject<Storage>()
    private val batteryStatus by inject<BatteryStatus>()

    private var dispatcher = Dispatchers.IO
    private val defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, throwable.message, throwable)
    }

    private val scope = CoroutineScope(SupervisorJob() + dispatcher + defaultErrorHandler)
    private var balanceTimer: Timer? = null
    private var endOfDayTimer: Timer? = null
    private var mobileLimitJob: Job? = null

    override fun onCreate() {
        //todo needs refactor
        observeNetworkUsage()
        observeNetworkStatus()
        startEndOfDayTimer()
        observeBatteryStatus()
        observeLimitStatus()
        super.onCreate()
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
            .setColor(Color.WHITE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    @Suppress("DEPRECATION")
    private fun stopForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            stopForeground(STOP_FOREGROUND_DETACH)
        } else {
            stopForeground(true)
        }
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

    private suspend fun startNode() = withContext(dispatcher) {
        nodeServiceDataSource.fetchIdentity()
        nodeServiceDataSource.fetchServices()
        nodeServiceDataSource.fetchBalance()
    }

    private fun registerListeners() {
        mobileNode.registerServiceStatusChangeCallback { _, _ ->
            scope.launch {
                nodeServiceDataSource.fetchServices()
            }
        }

        mobileNode.registerIdentityRegistrationChangeCallback { _, _ ->
            scope.launch {
                nodeServiceDataSource.fetchIdentity()
            }
        }
    }

    private fun startBalanceTimer() {
        balanceTimer?.cancel()
        balanceTimer = fixedRateTimer(
            initialDelay = BALANCE_CHECK_INTERVAL,
            period = BALANCE_CHECK_INTERVAL
        ) {
            scope.launch {
                nodeServiceDataSource.fetchBalance()
            }
        }
    }

    private fun observeNetworkUsage() {
        mobileLimitJob?.cancel()
        mobileLimitJob = networkReporter.monitorUsage(NetworkType.MOBILE)
            .onEach { nodeServiceDataSource.updateMobileDataUsage(it) }
            .launchIn(scope)
    }

    private fun observeNetworkStatus() = scope.launch {
        networkReporter.currentConnectivity.drop(1).collect {
            updateNodeServices()
        }
    }

    private fun startEndOfDayTimer() {
        endOfDayTimer?.cancel()
        endOfDayTimer = timer(
            startAt = Calendar.getInstance().nextDay(),
            period = TimeUnit.DAYS.toMillis(1)
        ) {
            resetMobileDataUsageIfNeeded()
        }
    }

    private fun observeBatteryStatus() = scope.launch {
        batteryStatus.isCharging.collect {
            updateNodeServices()
        }
    }

    private fun observeLimitStatus() = scope.launch {
        nodeServiceDataSource.limitMonitor.collect {
            updateNodeServices()
        }
    }

    private fun resetMobileDataUsageIfNeeded() {
        val calendar = Calendar.getInstance()
        val startDate = Date(storage.usage.startTime)
        val today = Date()
        if (calendar.isFirstDayOfMonth && today.after(startDate)) {
            storage.usage = NodeUsage(Date().time, 0)
        }
    }

    private suspend fun updateNodeServices() = withContext(dispatcher) {
        val config = storage.config
        val wifiOption = networkReporter.isConnected(NetworkType.WIFI)
        val mobileDataOption =
            config.useMobileData && networkReporter.isConnected(NetworkType.MOBILE)
        val batteryOption = if (config.allowUseOnBattery) true else batteryStatus.isCharging.value
        if (batteryOption && (wifiOption || mobileDataOption) && !isMobileLimitReached()) {
            mobileNode.startProvider()
        } else {
            mobileNode.stopProvider()
        }
    }

    private fun isMobileLimitReached(): Boolean {
        val config = storage.config
        val usage = storage.usage
        val shouldCheckLimit = config.useMobileData &&
                config.useMobileDataLimit &&
                networkReporter.isConnected(NetworkType.MOBILE)
        val limit = config.mobileDataLimit ?: return false
        return if (shouldCheckLimit) {
            usage.bytes > limit
        } else {
            false
        }
    }


    internal inner class Bridge : Binder(), NodeServiceBinder {

        override suspend fun start() {
            if (isStarted) return
            startNode()
            registerListeners()
            startBalanceTimer()
            isStarted = true
        }

        override fun startForegroundService() {
            startForegroundNotification()
        }

        override fun stopForegroundService() {
            stopForegroundNotification()
        }

        override suspend fun startServices() {
            updateNodeServices()
        }

        override suspend fun updateServices() {
            nodeServiceDataSource.updateMobileDataUsage(0)
            updateNodeServices()
        }

        override fun stopServices() {
            mobileNode.stopProvider()
        }

        override suspend fun stop() {
            isStarted = false
            mobileNode.stopProvider()
            mobileNode.shutdown()
        }
    }
}
