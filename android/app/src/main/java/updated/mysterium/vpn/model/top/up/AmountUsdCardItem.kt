package updated.mysterium.vpn.model.top.up

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class AmountUsdCardItem(
    open val amountUsd: Double,
    open var isSelected: Boolean
): Parcelable {

    fun changeState() {
        isSelected = !isSelected
    }

}
