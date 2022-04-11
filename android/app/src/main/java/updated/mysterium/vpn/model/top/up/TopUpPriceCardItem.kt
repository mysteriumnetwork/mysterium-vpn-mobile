package updated.mysterium.vpn.model.top.up

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TopUpPriceCardItem(
    val sku: String,
    val title: String,
    val price: Double,
    val mystAmount: Double,
    var isSelected: Boolean = false
): Parcelable {

    fun changeState() {
        isSelected = !isSelected
    }
}
