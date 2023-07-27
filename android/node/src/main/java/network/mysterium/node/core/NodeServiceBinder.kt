package network.mysterium.node.core

import android.os.IBinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import network.mysterium.node.model.NodeConfig
import network.mysterium.node.model.NodeIdentity
import network.mysterium.node.model.NodeServiceType

internal interface NodeServiceBinder : IBinder {

    suspend fun start()
    suspend fun stop()
    fun startForegroundService()
    fun stopForegroundService()
    suspend fun startServices()
    suspend fun updateServices()
    fun stopServices()
}
