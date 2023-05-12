package network.mysterium.node

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mysterium.MobileNode
import mysterium.Mysterium
import network.mysterium.node.model.NodeService
import network.mysterium.node.model.NodeTerms
import network.mysterium.terms.Terms

internal class MysteriumNode(
    private val context: Context
) : Node {

    private var node: MobileNode? = null

    override val terms: NodeTerms
        get() = NodeTerms(Terms.endUserMD(), Terms.version())

    override val nodeUIUrl: String = "http://localhost:4449"

    override suspend fun initialize() {
        node = Mysterium.newNode(
            context.filesDir.canonicalPath,
            Mysterium.defaultProviderNodeOptions()
        )
    }

    override suspend fun getServices(): Flow<List<NodeService>> {
        return flow {
            emit(
                listOf(
                    NodeService("wireguard", NodeService.Status.RUNNING),
                    NodeService("scraping", NodeService.Status.STARTING),
                    NodeService("data_transfer", NodeService.Status.NOT_RUNNING)
                )
            )
        }
    }

}
