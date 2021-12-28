package updated.mysterium.vpn.model.top.up

data class CurrencyCardItem(
    val currency: String,
    var isSelected: Boolean = false
) {

    fun changeState() {
        isSelected = !isSelected
    }
}
