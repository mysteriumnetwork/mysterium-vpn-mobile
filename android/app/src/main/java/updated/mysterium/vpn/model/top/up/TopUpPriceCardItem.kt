package updated.mysterium.vpn.model.top.up

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopUpPriceCardItem(
    val id: String,
    val sku: String,
    val price: Double,
    var isSelected: Boolean = false
): Parcelable {

    fun changeState() {
        isSelected = !isSelected
    }
}
