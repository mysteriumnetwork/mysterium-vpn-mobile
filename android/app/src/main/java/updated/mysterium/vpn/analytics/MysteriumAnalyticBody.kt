package updated.mysterium.vpn.analytics

data class MysteriumAnalyticBody(
    val eventName: String,
    val client: ClientBody? = null,
    val event: EventBody? = null
)
