package updated.mysterium.vpn.database.preferences

enum class SharedPreferencesList(val prefName: String) {
    BALANCE("BALANCE"),
    MIN_BALANCE("MIN_BALANCE"),
    BALANCE_PUSH("BALANCE_PUSH"),
    MIN_BALANCE_PUSH("MIN_BALANCE_PUSH"),
    LOGIN("LOGIN"),
    TOP_UP_FLOW("TOP_UP_FLOW"),
    ACCOUNT_CREATED("ACCOUNT_CREATED"),
    TERMS("TERMS"),
    DNS("DNS"),
    IDENTITY_ADDRESS("IDENTITY_ADDRESS"),
    LANGUAGE("LANGUAGE")
}
