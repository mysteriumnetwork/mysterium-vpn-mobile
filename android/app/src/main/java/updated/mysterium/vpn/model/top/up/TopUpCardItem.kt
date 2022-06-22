package updated.mysterium.vpn.model.top.up

data class TopUpCardItem(
    val mystAmount: Double,
    val amountUSD: Double,
    var isSelected: Boolean = false
) {

    fun changeState() {
        isSelected = !isSelected
    }
}
