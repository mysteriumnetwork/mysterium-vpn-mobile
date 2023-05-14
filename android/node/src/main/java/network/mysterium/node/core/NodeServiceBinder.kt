package network.mysterium.node.core

import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import mysterium.MobileNode
import network.mysterium.node.model.NodeIdentity
import network.mysterium.node.model.NodeServiceType

internal interface NodeServiceBinder : IBinder {
    val identity: Flow<NodeIdentity>
    val services: Flow<List<NodeServiceType>>
    val balance: Flow<Double>
    suspend fun start()
    fun startForeground()
    fun stop()
}