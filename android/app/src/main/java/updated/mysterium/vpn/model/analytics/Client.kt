package updated.mysterium.vpn.model.analytics

data class Client(
    val machineID: String,
    val appVersion: String,
    val os: String = "android",
    val osVersion: String,
    val country: String,
    val consumerID: String
)
