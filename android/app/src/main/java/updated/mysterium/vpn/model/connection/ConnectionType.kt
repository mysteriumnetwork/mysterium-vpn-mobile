package updated.mysterium.vpn.model.connection

enum class ConnectionType(val type: String) {
    MANUAL_CONNECT("ManualConnect"),
    SMART_CONNECT("SmartConnect");

    companion object {
        fun from(type: String): ConnectionType {
            return values().find { it.type == type } ?: MANUAL_CONNECT
        }
    }
}
