package updated.mysterium.vpn.model.manual.connect

enum class ConnectionState(val state: String) {
    NOTCONNECTED("NotConnected"),
    CONNECTING("Connecting"),
    CONNECTED("Connected"),
    DISCONNECTING("Disconnecting"),
    ONHOLD("OnHold")
}
