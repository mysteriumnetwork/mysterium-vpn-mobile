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

    // Identity is considered as registered if it's status RegisteredConsumer or InProgress since we support fast
    // identity registration flow which means there is no need to wait for actual registration.
    val isRegistered: Boolean
        get() {
            return status == Status.REGISTERED || status == Status.IN_PROGRESS
        }
}