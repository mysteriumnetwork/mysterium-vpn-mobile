package updated.mysterium.vpn.model.top.up

import androidx.annotation.RawRes

data class CryptoCardItem(
    val value: String,
    val isLightningAvailable: Boolean,
    @RawRes val animationRaw: Int,
    var isSelected: Boolean = false
) {

    fun changeState() {
        isSelected = !isSelected
    }
}
