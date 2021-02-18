package network.mysterium.service.core

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import mysterium.MobileNode

// DeferredNode is a wrapper class which holds MobileNode instance promise.
// This allows to load UI without waiting for node to start.
class DeferredNode {
    private var deferredNode = CompletableDeferred<MobileNode>()

    suspend fun await(): MobileNode {
        return deferredNode.await()
    }

    fun startedOrStarting(): Boolean {
        return deferredNode.isCompleted || lock.availablePermits < 1
    }

    private val lock = Semaphore(1)

    fun start(service: MysteriumCoreService, done: ((err: Exception?) -> Unit)? = null) {
        if (!lock.tryAcquire()) {
            Log.i(TAG, "Node is already started or starting, skipping")
            return
        }
        val startJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                val node = service.startNode()
                deferredNode.complete(node)
                done?.invoke(null)
            } catch (err: Exception) {
                Log.e(TAG, "Failed to start node", err)
                done?.invoke(err)
            }
        }
        startJob.invokeOnCompletion {
            lock.release()
        }
    }

    companion object {
        const val TAG = "DeferredNode"
    }
}
