package updated.mysterium.vpn.analitics

enum class AnalyticEvent(val eventName: String) {
    LOGIN("LOGIN"),
    NEW_PUSHY_DEVICE("NEW_PUSHY_DEVICE"),
    VPN_TIME("VPN_TIME"),
    NEW_SESSION("NEW_SESSION"),
    COUNTRY_SELECTED("COUNTRY_SELECTED"),
    PAYMENT("PAYMENT"),
    REFERRAL_TOKEN("REFERRAL_TOKEN")
}
