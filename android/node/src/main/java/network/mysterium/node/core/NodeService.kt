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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import mysterium.GetBalanceRequest
import mysterium.GetIdentityRequest
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.node.Storage
import network.mysterium.node.StorageFactory
import network.mysterium.node.battery.BatteryStatus
import network.mysterium.node.extensions.isFirstDayOfMonth
import network.mysterium.node.extensions.nextDay
import network.mysterium.node.model.NodeIdentity
import network.mysterium.node.model.NodeServiceType
import network.mysterium.node.model.NodeUsage
import network.mysterium.node.network.NetworkReporter
import network.mysterium.node.network.NetworkType
import network.mystrium.node.R
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

    private lateinit var networkReporter: NetworkReporter
    private lateinit var storage: Storage
    private lateinit var batteryStatus: BatteryStatus
    private var mobileNode: MobileNode? = null
    private var servicesFlow = MutableStateFlow<List<NodeServiceType>>(emptyList())
    private var identityFlow = MutableStateFlow(NodeIdentity.empty())
    private var balanceFlow = MutableStateFlow(0.0)
    private var limitMonitorFlow = MutableStateFlow(false)
    private var dispatcher = Dispatchers.IO
    private val scope = CoroutineScope(dispatcher)
    private var balanceTimer: Timer? = null
    private var endOfDayTimer: Timer? = null
    private var mobileLimitJob: Job? = null

    override fun onCreate() {
        networkReporter = NetworkReporter(this)
        storage = StorageFactory.make(this)
        batteryStatus = BatteryStatus(this)
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
        createNode()
        fetchIdentity()
        fetchServices()
        fetchBalance()
    }

    private suspend fun createNode() = withContext(dispatcher) {
        val node = Mysterium.newNode(
            filesDir.canonicalPath,
            Mysterium.defaultProviderNodeOptions()
        )
        mobileNode = node
    }

    private suspend fun fetchServices() = withContext(dispatcher) {
        val node = mobileNode ?: return@withContext
        val json = node.allServicesState.decodeToString()
        val services = Json.decodeFromString<List<NodeServiceType>>(json)
        servicesFlow.update {
            services.toSet().toList()
        }
    }

    private suspend fun fetchIdentity() = withContext(dispatcher) {
        val node = mobileNode ?: return@withContext
        val identity = node.getIdentity(GetIdentityRequest())
        identityFlow.update {
            NodeIdentity(
                identity.identityAddress,
                identity.channelAddress,
                NodeIdentity.Status.from(identity.registrationStatus)
            )
        }
    }

    private suspend fun fetchBalance() = withContext(dispatcher) {
        val node = mobileNode ?: return@withContext
        val request = GetBalanceRequest()
        request.identityAddress = identityFlow.value.address
        val response = node.getUnsettledEarnings(request)
        balanceFlow.update {
            response.balance
        }
    }

    private fun registerListeners() {
        val node = mobileNode ?: return

        node.registerServiceStatusChangeCallback { _, _ ->
            scope.launch {
                fetchServices()
            }
        }

        node.registerIdentityRegistrationChangeCallback { _, _ ->
            scope.launch {
                fetchIdentity()
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
                fetchBalance()
            }
        }
    }

    private fun observeNetworkUsage() {
        mobileLimitJob?.cancel()
        mobileLimitJob = scope.launch {
            networkReporter.monitorUsage(NetworkType.MOBILE)
                .collect {
                    updateMobileDataUsage(it)
                }
        }
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
        limitMonitorFlow.collect {
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

    private fun updateMobileDataUsage(usedBytes: Long) {
        val config = storage.config
        if (config.useMobileData &&
            config.useMobileDataLimit &&
            config.mobileDataLimit != null &&
            networkReporter.isConnected(NetworkType.MOBILE)
        ) {
            var usage = storage.usage
            usage = usage.copy(bytes = usage.bytes + usedBytes)
            storage.usage = usage
            if (usage.bytes > config.mobileDataLimit) {
                limitMonitorFlow.update { true }
                mobileNode?.stopProvider()
            } else {
                limitMonitorFlow.update { false }
            }
            Log.d(TAG, "Total usage: ${usage.bytes / (1024 * 1024)} MB")
        } else {
            limitMonitorFlow.update { false }
        }
    }

    private suspend fun updateNodeServices() = withContext(dispatcher) {
        val config = storage.config
        val wifiOption = networkReporter.isConnected(NetworkType.WIFI)
        val mobileDataOption =
            config.useMobileData && networkReporter.isConnected(NetworkType.MOBILE)
        val batteryOption = if (config.allowUseOnBattery) true else batteryStatus.isCharging.value
        if (batteryOption && (wifiOption || mobileDataOption) && !isMobileLimitReached()) {
            // starting and stopping provider helps to display service status correctly
            mobileNode?.stopProvider()
            mobileNode?.startProvider()
        } else {
            mobileNode?.stopProvider()
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

        override val services: Flow<List<NodeServiceType>>
            get() = servicesFlow.asStateFlow()

        override val balance: Flow<Double>
            get() = balanceFlow.asStateFlow()

        override val limitMonitor: StateFlow<Boolean>
            get() = limitMonitorFlow.asStateFlow()

        override val identity: StateFlow<NodeIdentity>
            get() = identityFlow.asStateFlow()

        override suspend fun start() {
            if (mobileNode != null) {
                return
            }
            startNode()
            registerListeners()
            startBalanceTimer()
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
            updateMobileDataUsage(0)
            updateNodeServices()
        }

        override fun stopServices() {
            mobileNode?.stopProvider()
        }

        override suspend fun stop() {
            mobileNode?.stopProvider()
            mobileNode?.shutdown()
        }
    }
}
