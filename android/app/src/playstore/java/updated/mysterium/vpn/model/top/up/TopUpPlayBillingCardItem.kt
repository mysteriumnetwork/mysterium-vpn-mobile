package updated.mysterium.vpn.model.top.up

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TopUpPlayBillingCardItem(
    val id: String,
    val sku: String,
    val amountUsd: Double,
    var isSelected: Boolean = false
): Parcelable {

    fun changeState() {
        isSelected = !isSelected
    }
}
