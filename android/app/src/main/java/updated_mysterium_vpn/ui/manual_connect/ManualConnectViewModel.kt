package updated_mysterium_vpn.ui.manual_connect

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.service.core.NodeRepository

class ManualConnectViewModel(
        private val nodeRepository: NodeRepository
) : ViewModel() {

    private val deferredNode = DeferredNode()

    fun startDeferredNode(deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        CoroutineScope(Dispatchers.Main).launch {
            deferredMysteriumCoreService.await()
            if (!deferredNode.startedOrStarting()) {
                deferredNode.start(deferredMysteriumCoreService.await())
            }
        }
        setDeferredNode()
    }

    private fun setDeferredNode() {
        nodeRepository.deferredNode = deferredNode
    }
}
