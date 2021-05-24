package updated.mysterium.vpn.model.wallet

enum class IdentityRegistrationStatus(val status: String) {
    UNKNOWN("Unknown"),
    REGISTERED("Registered"),
    UNREGISTERED("Unregistered"),
    IN_PROGRESS("InProgress"),
    REGISTRATION_ERROR("RegistrationError");

    companion object {
        fun parse(status: String): IdentityRegistrationStatus {
            return values().find { it.status == status } ?: UNKNOWN
        }
    }
}
