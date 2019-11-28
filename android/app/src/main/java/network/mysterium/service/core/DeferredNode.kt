package network.mysterium.service.core

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mysterium.MobileNode

// DeferredNode is a wrapper class which holds MobileNode instance promise.
// This allows to load UI without waiting for node to start.
class DeferredNode {
    private var deferredNode = CompletableDeferred<MobileNode>()

    suspend fun await(): MobileNode {
        return deferredNode.await()
    }

    fun start(service: MysteriumCoreService) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val node = service.startNode()
                deferredNode.complete(node)
            } catch (tr: Throwable) {
                Log.e(TAG, "Failed to start node", tr)
            }
        }
    }

    companion object {
        const val TAG = "DeferredNode"
    }
}