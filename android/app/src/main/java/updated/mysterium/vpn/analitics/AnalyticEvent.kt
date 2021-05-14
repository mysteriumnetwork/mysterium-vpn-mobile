package updated.mysterium.vpn.analitics

enum class AnalyticEvent(val eventName: String) {
    LOGIN("LOGIN"),
    VPN_TIME("VPN_TIME"),
    NEW_SESSION("NEW_SESSION"),
    COUNTRY_SELECTED("COUNTRY_SELECTED"),
    PAYMENT("PAYMENT")
}
