package updated.mysterium.vpn.analytics

data class EventBody(
    val name: String,
    val duration: Double?,
    val balance: Double?,
    val country: String?,
    val providerID: String?,
    val pageTitle: String
)
