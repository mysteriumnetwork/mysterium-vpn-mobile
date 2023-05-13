package network.mysterium.node.core

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.mysterium.node.Storage
import network.mysterium.node.model.NodeRunnerConfig

internal class StorageImpl(
    context: Context
) : Storage {

    private companion object {
        const val IS_REGISTERED = "isRegistered"
        const val NODE_CONFIG = "nodeConfig"
    }

    private val preferences = context.getSharedPreferences("mysterium.node", Context.MODE_PRIVATE)

    override var isRegistered: Boolean
        get() = preferences.getBoolean(IS_REGISTERED, false)
        set(value) {
            preferences.edit()
                .putBoolean(IS_REGISTERED, value)
                .apply()
        }

    override var config: NodeRunnerConfig
        get() = decode(NODE_CONFIG) ?: NodeRunnerConfig()
        set(value) {
            encode(NODE_CONFIG, value)
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