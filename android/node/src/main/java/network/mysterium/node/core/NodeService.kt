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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import mysterium.GetBalanceRequest
import mysterium.GetIdentityRequest
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.node.model.NodeIdentity
import network.mysterium.node.model.NodeServiceType
import network.mystrium.node.R
import java.sql.Time
import java.util.Date
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer

class NodeService : Service() {

    companion object {
        const val CHANNEL_ID = "mystnodes.channel"
        const val NOTIFICATION_ID = 1
        val BALANCE_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(10)
    }

    private var mobileNode: MobileNode? = null
    private var servicesFlow = MutableStateFlow<List<NodeServiceType>>(emptyList())
    private var identityFlow = MutableStateFlow(
        NodeIdentity(
            "",
            "",
            NodeIdentity.Status.UNKNOWN
        )
    )
    private var balanceFlow = MutableStateFlow(0.0)

    private var dispatcher = Dispatchers.IO
    private val scope = CoroutineScope(dispatcher)
    private var balanceTimer: Timer? = null

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

    private suspend fun startNode() = withContext(dispatcher) {
        createNode()
        fetchIdentity()
        fetchServices()
        fetchBalance()
        registerListeners()
        startBalanceTimer()
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
        servicesFlow.tryEmit(Json.decodeFromString(json))
    }

    private suspend fun fetchIdentity() = withContext(dispatcher) {
        val node = mobileNode ?: return@withContext
        val identity = node.getIdentity(GetIdentityRequest())
        identityFlow.tryEmit(
            NodeIdentity(
                identity.identityAddress,
                identity.channelAddress,
                NodeIdentity.Status.from(identity.registrationStatus)
            )
        )
    }

    private suspend fun fetchBalance() = withContext(dispatcher) {
        val node = mobileNode ?: return@withContext
        val request = GetBalanceRequest()
        request.identityAddress = identityFlow.value.address
        val response = node.getUnsettledEarnings(request)
        balanceFlow.tryEmit(response.balance)
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

    internal inner class Bridge : Binder(), NodeServiceBinder {

        override val services: Flow<List<NodeServiceType>>
            get() = servicesFlow.asStateFlow()

        override val balance: Flow<Double>
            get() = balanceFlow.asStateFlow()

        override val identity: Flow<NodeIdentity>
            get() = identityFlow.asStateFlow()

        override suspend fun start() {
            if (mobileNode != null) {
                return
            }
            startNode()
        }

        override fun startForeground() {
            startForegroundNotification()
        }

        override fun stop() {
        }
    }
}