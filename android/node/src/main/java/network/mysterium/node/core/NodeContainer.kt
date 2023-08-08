package network.mysterium.node.core

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import mysterium.MobileNode
import mysterium.Mysterium

//workaround to inject mobile node
class NodeContainer(private val context: Context) {

    private var mobileNode: MobileNode? = null

    private val mutex = Mutex()
    suspend fun getInstance(): MobileNode = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (mobileNode != null) {
                mobileNode!!
            } else {
                mobileNode = Mysterium.newNode(
                    context.filesDir.canonicalPath,
                    Mysterium.defaultProviderNodeOptions()
                )
                mobileNode!!
            }
        }
    }
}
