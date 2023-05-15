package network.mysterium.node.core

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.mysterium.node.Storage
import network.mysterium.node.model.NodeConfig
import network.mysterium.node.model.NodeUsage
import java.util.Date

internal class StorageImpl(
    context: Context
) : Storage {

    private companion object {
        const val IS_REGISTERED = "isRegistered"
        const val NODE_CONFIG = "nodeConfig"
        const val NODE_USAGE = "nodeUsage"
    }

    private val preferences = context.getSharedPreferences("mysterium.node", Context.MODE_PRIVATE)

    override var isRegistered: Boolean
        get() = preferences.getBoolean(IS_REGISTERED, false)
        set(value) {
            preferences.edit()
                .putBoolean(IS_REGISTERED, value)
                .apply()
        }

    override var config: NodeConfig
        get() = decode(NODE_CONFIG) ?: NodeConfig()
        set(value) {
            encode(NODE_CONFIG, value)
        }
    override var usage: NodeUsage
        get() = decode(NODE_USAGE) ?: NodeUsage(Date().time, 0)
        set(value) {
            encode(NODE_USAGE, value)
        }

    private inline fun <reified T> decode(key: String): T? {
        val string = preferences.getString(key, null) ?: return null
        return Json.decodeFromString(string)
    }

    private inline fun <reified T> encode(key: String, value: T) {
        preferences.edit()
            .putString(key, Json.encodeToString(value))
            .apply()
    }
}