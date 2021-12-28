package updated.mysterium.vpn.model.payment

enum class PaymentCurrency(val symbol: Char, val currency: String) {
    EUR('€', "EUR"),
    USD('$', "USD"),
    GBP('£', "GBP");

    companion object {

        fun from(currency: String) = values().firstOrNull {
            it.currency == currency
        }
    }
}
