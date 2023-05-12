package network.mysterium.node

import android.content.Context

object NodeFactory {
    fun make(context: Context): Node {
        return MysteriumNode(context)
    }
}
