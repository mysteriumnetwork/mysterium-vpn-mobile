package updated.mysterium.vpn.model.analytics

data class EventInfo(
    val eventName: String,
    val duration: String?,
    val balance: Double?,
    val country: String?,
    val providerID: String?,
    val pageTitle: String?,
)
