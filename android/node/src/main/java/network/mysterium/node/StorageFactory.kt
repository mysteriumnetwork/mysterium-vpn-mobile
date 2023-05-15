package network.mysterium.node

import android.content.Context
import network.mysterium.node.core.StorageImpl

internal object StorageFactory {
    fun make(context: Context): Storage {
        return StorageImpl(context)
    }
}