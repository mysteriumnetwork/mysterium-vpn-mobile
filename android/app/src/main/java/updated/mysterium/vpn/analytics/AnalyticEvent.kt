package updated.mysterium.vpn.analytics

enum class AnalyticEvent(val eventName: String) {
    STARTUP("startup"),
    CONNECT_ATTEMPT("connect_attempt"),
    CONNECT_SUCCESS("connect_success"),
    CONNECT_FAILURE("connect_failure"),
    MANUAL_CONNECT("manual_connect"),
    QUICK_CONNECT("quick_connect"),
    DISCONNECT_ATTEMPT("disconnect_attempt"),
    DISCONNECT_SUCCESS("disconnect_success"),
    DISCONNECT_FAILURE("disconnect_failure"),
    PAGE_VIEW("page_view"),
    BALANCE_UPDATE("balance_update")
}
