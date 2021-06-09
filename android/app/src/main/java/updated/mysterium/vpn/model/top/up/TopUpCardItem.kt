package updated.mysterium.vpn.model.top.up

data class TopUpCardItem(
    val value: String,
    var isSelected: Boolean = false
) {

    fun changeState() {
        isSelected = !isSelected
    }
}
