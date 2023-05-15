package network.mysterium.node.model

data class NodeIdentity(
    val address: String,
    val channelAddress: String,
    val status: Status
) {
    companion object {
        fun empty() = NodeIdentity("", "", Status.UNKNOWN)
    }

    enum class Status(private val raw: String) {
        UNKNOWN("Unknown"),
        REGISTERED("Registered"),
        UNREGISTERED("Unregistered"),
        IN_PROGRESS("InProgress"),
        REGISTRATION_ERROR("RegistrationError");

        companion object {
            fun from(raw :String): Status {
                return values().find { it.raw == raw } ?: UNKNOWN
            }
        }
    }
}