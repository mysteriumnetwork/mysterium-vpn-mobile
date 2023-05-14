package network.mysterium.node.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import network.mysterium.node.Node
import network.mysterium.node.Storage
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
    private val storage: Storage
) : Node {

    private companion object {
        val TAG: String = NodeImpl::class.java.simpleName
    }

    override val terms: NodeTerms
        get() = NodeTerms(Terms.endUserMD(), Terms.version())

    override val nodeUIUrl: String = "http://localhost:4449"

    override val isRegistered: Boolean
        get() = storage.isRegistered

    override var config: NodeConfig
        get() = storage.config
        set(value) {
            storage.config = value
        }
    override val services: Flow<List<NodeServiceType>>
        get() = service?.services
            ?: throw IllegalStateException("Node should be started to get services")

    override val balance: Flow<Double>
        get() = service?.balance
            ?: throw IllegalStateException("Node should be started to get services")

    override val identity: Flow<NodeIdentity>
        get() = service?.identity
            ?: throw IllegalStateException("Node should be started to get services")

    private var service: NodeServiceBinder? = null

    override suspend fun start() {
        service = startService()
        service?.start()
    }

    override fun startServices() {
        service?.startForeground()
    }

    override fun stopServices() {
        // to implement
    }

    override suspend fun stop() {
        // to implement
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
