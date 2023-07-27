package network.mysterium.node.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import network.mysterium.node.Node
import network.mysterium.node.Storage
import network.mysterium.node.data.NodeServiceDataSource
import network.mysterium.node.model.NodeConfig
import network.mysterium.node.model.NodeIdentity
import network.mysterium.node.model.NodeServiceType
import network.mysterium.node.model.NodeTerms
import network.mysterium.terms.Terms
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class NodeImpl(
    private val context: Context,
    private val storage: Storage,
    private val dataSource: NodeServiceDataSource,
) : Node {

    private companion object {
        val TAG: String = NodeImpl::class.java.simpleName
    }

    override val terms: NodeTerms
        get() = NodeTerms(Terms.endUserMD(), Terms.version())

    override val nodeUIUrl: String = "http://localhost:4449"

    override val isRegistered: Boolean
        get() = storage.isRegistered

    override val config: NodeConfig
        get() = storage.config

    override val services: Flow<List<NodeServiceType>>
        get() = dataSource.services

    override val balance: Flow<Double>
        get() = dataSource.balance

    override val identity: StateFlow<NodeIdentity>
        get() = dataSource.identity

    override val limitMonitor: StateFlow<Boolean>
        get() = dataSource.limitMonitor

    override suspend fun updateConfig(config: NodeConfig) {
        storage.config = config
        service?.updateServices()
    }

    private var service: NodeServiceBinder? = null

    override suspend fun start() {
        val service = startService()
        service.start()
        if (dataSource.identity.value.status == NodeIdentity.Status.REGISTERED) {
            service.startForegroundService()
            service.startServices()
        }
        this.service = service
    }

    override suspend fun enableForegroundService() {
        service?.startForegroundService()
        service?.updateServices()
    }

    override fun disableForegroundService() {
        service?.stopForegroundService()
    }

    override suspend fun stop() {
        disableForegroundService()
        service?.stop()
    }

    private suspend fun startService() = suspendCoroutine { continuation ->
        val intent = Intent(context, NodeService::class.java)
        context.bindService(
            intent,
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                    val service = binder as? NodeServiceBinder

                    if (service == null) {
                        continuation.resumeWithException(IllegalStateException("Unable to create service"))
                        return
                    }
                    continuation.resume(service)
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                }
            },
            Context.BIND_AUTO_CREATE
        )
    }
}
