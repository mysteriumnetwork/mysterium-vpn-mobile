package updated.mysterium.vpn.model.manual.connect

enum class ConnectionStateModel(val state: String) {
    NOTCONNECTED("NotConnected"),
    CONNECTING("Connecting"),
    CONNECTED("Connected"),
    DISCONNECTING("Disconnecting")
}
