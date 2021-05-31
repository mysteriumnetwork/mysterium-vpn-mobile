package updated.mysterium.vpn.model.manual.connect

enum class ConnectionState(val state: String) {
    NOTCONNECTED("NotConnected"),
    CONNECTING("Connecting"),
    CONNECTED("Connected"),
    DISCONNECTING("Disconnecting"),
    ON_HOLD("OnHold"),
    IP_NOT_CHANGED("IPNotChanged");

    companion object {

        fun from(value: String) = values().find {
            it.state == value
        } ?: NOTCONNECTED
    }
}
