package updated.mysterium.vpn.model.top.up

data class TopUpAmountCardItem(
    val value: String,
    var isSelected: Boolean = false
) {

    fun changeState() {
        isSelected = !isSelected
    }
}
