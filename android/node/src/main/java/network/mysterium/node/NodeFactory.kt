package network.mysterium.node

import android.content.Context
import network.mysterium.node.core.NodeImpl
import network.mysterium.node.core.StorageImpl

object NodeFactory {
    fun make(context: Context): Node {
        return NodeImpl(
            context,
            StorageFactory.make(context)
        )
    }
}
