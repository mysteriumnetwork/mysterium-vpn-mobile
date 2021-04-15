package updated.mysterium.vpn.database.preferences

enum class SharedPreferencesList(val prefName: String) {
    BALANCE("BALANCE"),
    MIN_BALANCE("MIN_BALANCE"),
    BALANCE_PUSH("BALANCE_PUSH"),
    MIN_BALANCE_PUSH("MIN_BALANCE_PUSH"),
    LOGIN("LOGIN"),
    ACCOUNT("ACCOUNT"),
    TERMS("TERMS"),
    DNS("DNS"),
    IDENTITY_ADDRESS("IDENTITY_ADDRESS")
}
