package network.mysterium.node.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.node.Node
import network.mysterium.node.Storage
import network.mysterium.node.model.NodeRunnerConfig
import network.mysterium.node.model.NodeRunnerService
import network.mysterium.node.model.NodeRunnerTerms
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

    override val terms: NodeRunnerTerms
        get() = NodeRunnerTerms(Terms.endUserMD(), Terms.version())

    override val nodeUIUrl: String = "http://localhost:4449"

    override val isRegistered: Boolean
        get() = storage.isRegistered

    override var config: NodeRunnerConfig
        get() = storage.config
        set(value) {
            storage.config = value
        }

    private var service: NodeServiceBinder? = null

    override suspend fun start() = suspendCoroutine { continuation ->
        val intent = Intent(context, NodeService::class.java)
        context.bindService(
            intent,
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                    val service = binder as? NodeServiceBinder ?: return
                    try {
                        service.start()

                        this@NodeImpl.service = service

                        if (isRegistered) {
                            enableForeground()
                        }

                        continuation.resume(Unit)
                    } catch (error: Throwable) {
                        continuation.resumeWithException(error)
                    }
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                }
            },
            Context.BIND_AUTO_CREATE
        )
    }

    override fun enableForeground() {
        storage.isRegistered = true
        service?.startForeground()
    }

    override suspend fun stop() {
        // to implement
    }

    override suspend fun getServices(): Flow<List<NodeRunnerService>> {
        return flow {
            emit(
                listOf(
                    NodeRunnerService("wireguard", NodeRunnerService.Status.RUNNING),
                    NodeRunnerService("scraping", NodeRunnerService.Status.STARTING),
                    NodeRunnerService("data_transfer", NodeRunnerService.Status.NOT_RUNNING)
                )
            )
        }
    }
}
