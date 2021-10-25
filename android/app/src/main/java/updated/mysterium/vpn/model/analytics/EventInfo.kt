package updated.mysterium.vpn.model.analytics

interface EventInfo {
    val eventName: String
    val duration: Long?
    val balance: Double?
    val country: String?
    val providerID: String?
    val pageTitle: String?
}
