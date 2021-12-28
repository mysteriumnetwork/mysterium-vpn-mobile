package updated.mysterium.vpn.model.top.up

import updated.mysterium.vpn.model.payment.PaymentCurrency

data class CurrencyCardItem(
    val currency: PaymentCurrency?,
    var isSelected: Boolean = false
) {

    fun changeState() {
        isSelected = !isSelected
    }
}
