package updated.mysterium.vpn.model.pushy

enum class PushyTopic(val topic: String) {
    PAYMENT_TRUE("Payment_True"),
    PAYMENT_FALSE("Payment_False"),
    REFERRAL_CODE_USED("Referral_Code_Used"),
    REFERRAL_CODE_NOT_USED("Referral_Code_Not_Used"),
    LESS_THEN_HALF_MYST("Less_Than_0.5_MYST")
}
