package updated.mysterium.vpn.model.manual.connect

data class ProviderState(
    val active: Boolean,
)

class ServiceStatus(
        val service: String,
        val status: String
)
