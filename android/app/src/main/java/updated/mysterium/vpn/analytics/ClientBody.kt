package updated.mysterium.vpn.analytics

data class ClientBody(
    val machineID: String?,
    val appVersion: String?,
    val os: String = "android",
    val osVersion: String?,
    val country: String?,
    val consumerID: String?
)
