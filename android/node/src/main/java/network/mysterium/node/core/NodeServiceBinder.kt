package network.mysterium.node.core

import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import network.mysterium.node.model.NodeConfig
import network.mysterium.node.model.NodeIdentity
import network.mysterium.node.model.NodeServiceType

internal interface NodeServiceBinder : IBinder {
    val identity: StateFlow<NodeIdentity>
    val services: Flow<List<NodeServiceType>>
    val balance: Flow<Double>
    val limitMonitor: StateFlow<Boolean>
    suspend fun start()
    fun stop()
    fun startForegroundService()
    suspend fun startServices()
    suspend fun updateServices()
    fun stopServices()
    fun resetMobileUsage()
}