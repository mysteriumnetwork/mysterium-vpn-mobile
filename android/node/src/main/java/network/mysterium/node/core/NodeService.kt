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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mysterium.MobileNode
import network.mysterium.node.Storage
import network.mysterium.node.analytics.NodeAnalytics
import network.mysterium.node.analytics.event.AnalyticsEvent
import network.mysterium.node.battery.BatteryStatus
import network.mysterium.node.data.NodeServiceDataSource
import network.mysterium.node.extensions.isFirstDayOfMonth
import network.mysterium.node.extensions.nextDay
import network.mysterium.node.model.NodeIdentity
import network.mysterium.node.model.NodeServiceType
import network.mysterium.node.model.NodeUsage
import network.mysterium.node.network.NetworkReporter
import network.mysterium.node.network.NetworkType
import network.mysterium.node.utils.cancelCatching
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

    private val nodeContainer by inject<NodeContainer>()
    private var mobileNode: MobileNode? = null
    private val nodeServiceDataSource by inject<NodeServiceDataSource>()
    private val networkReporter by inject<NetworkReporter>()
    private val storage by inject<Storage>()
    private val batteryStatus by inject<BatteryStatus>()
    private val analytics by inject<NodeAnalytics>()

    private var dispatcher = Dispatchers.IO
    private val defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, throwable.message, throwable)
    }

    private val isNotificationShown: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val scope = CoroutineScope(SupervisorJob() + dispatcher + defaultErrorHandler)
    private var balanceTimer: Timer? = null
    private var endOfDayTimer: Timer? = null
    private var mobileLimitJob: Job? = null

    override fun onCreate() {
        //todo needs refactor
        scope.launch {
            mobileNode = nodeContainer.getInstance()
        }
        scope.launch {
            nodeServiceDataSource.services.collectLatest {
                if (it.any { it.state == NodeServiceType.State.STARTING || it.state == NodeServiceType.State.RUNNING }) {
                    updateNodeServices(true)
                }
            }
        }
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        isNotificationShown.value = true
    }

    @Suppress("DEPRECATION")
    private fun stopForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        isNotificationShown.value = false
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

    private fun startNode() = scope.launch {
        nodeServiceDataSource.fetchServices()
        nodeServiceDataSource.fetchBalance()
        nodeServiceDataSource.fetchIdentity()
    }

    private fun registerListeners() {
        scope.launch {
            mobileNode = nodeContainer.getInstance()

            mobileNode?.registerServiceStatusChangeCallback { _, _ ->
                scope.launch {
                    nodeServiceDataSource.fetchServices()
                }
            }

            mobileNode?.registerIdentityRegistrationChangeCallback { _, _ ->
                scope.launch {
                    nodeServiceDataSource.fetchIdentity()
                }
            }
        }
    }

    private fun observeNotification() {
        combine(
            isNotificationShown,
            nodeServiceDataSource.identity
        ) { isNotificationShown, identity ->
            if (!isNotificationShown && identity.status == NodeIdentity.Status.REGISTERED) {
                updateNodeServices()
                startForegroundNotification()
            }
        }
            .launchIn(scope)
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
        mobileLimitJob?.cancelCatching()
        mobileLimitJob = networkReporter.monitorUsage(NetworkType.MOBILE)
            .onEach { nodeServiceDataSource.updateMobileDataUsage(it) }
            .launchIn(scope)
    }

    private fun observeNetworkStatus() = scope.launch {
        //delay to guarantee network off/on state
        networkReporter.currentConnectivity.onEach { delay(2000) }.collectLatest {
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

    private suspend fun updateNodeServices(isSkipStart: Boolean = false) = withContext(dispatcher) {
        val config = storage.config
        val wifiOption = networkReporter.isConnected(NetworkType.WIFI)
        val mobileDataOption =
            config.useMobileData && networkReporter.isConnected(NetworkType.MOBILE)
        val batteryOption = if (config.allowUseOnBattery) true else batteryStatus.isCharging.value
        if (batteryOption && (wifiOption || (mobileDataOption && !isMobileLimitReached()))) {
            if (!isSkipStart) {
                mobileNode?.startProvider()
                analytics.trackEvent(AnalyticsEvent.ToggleAnalyticsEvent.NodeUiState(isEnabled = true))
            }
        } else {
            mobileNode?.stopProvider()
            analytics.trackEvent(AnalyticsEvent.ToggleAnalyticsEvent.NodeUiState(isEnabled = false))
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

    private fun stopSelfService() {
        this.stopSelf()
    }


    internal inner class Bridge : Binder(), NodeServiceBinder {

        override fun start() {
            if (isStarted) return
            observeNotification()
            registerListeners()
            startBalanceTimer()
            startNode()
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
            mobileNode?.stopProvider()
            analytics.trackEvent(AnalyticsEvent.ToggleAnalyticsEvent.NodeUiState(isEnabled = false))
        }

        override suspend fun stop() {
            isStarted = false
            analytics.trackEvent(AnalyticsEvent.ToggleAnalyticsEvent.NodeUiState(isEnabled = false))
            mobileNode?.stopProvider()
            mobileNode?.shutdown()
        }

        override fun stopSelf() {
            stopSelfService()
        }
    }

}
