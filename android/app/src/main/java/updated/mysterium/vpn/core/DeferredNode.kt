package updated.mysterium.vpn.core

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import mysterium.MobileNode

// DeferredNode is a wrapper class which holds MobileNode instance promise.
// This allows to load UI without waiting for node to start.
class DeferredNode {

    private companion object {
        const val TAG = "DeferredNode"
    }

    private var deferredNode = CompletableDeferred<MobileNode>()
    private val lock = Semaphore(1)

    suspend fun await() = deferredNode.await()

    fun startedOrStarting() = deferredNode.isCompleted || lock.availablePermits < 1

    fun start(
        service: MysteriumCoreService,
        done: ((err: Exception?) -> Unit)? = null
    ) {
        if (!lock.tryAcquire()) {
            Log.i(TAG, "Node is already started or starting, skipping")
        } else {
            val handler = CoroutineExceptionHandler { _, exception ->
                Log.e(TAG, exception.localizedMessage ?: exception.toString())
                done?.invoke(exception as Exception)
            }
            val startJob = CoroutineScope(Dispatchers.Main + handler).launch {
                val node = service.startNode()
                deferredNode.complete(node)
                done?.invoke(null)
            }
            startJob.invokeOnCompletion {
                lock.release()
            }
        }
    }
}
