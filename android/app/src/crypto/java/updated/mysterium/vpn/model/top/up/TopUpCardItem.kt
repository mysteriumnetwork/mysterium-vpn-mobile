package updated.mysterium.vpn.model.top.up

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopUpCardItem(
    override val amountUsd: Double,
    override var isSelected: Boolean = false
) : AmountUsdCardItem(amountUsd, isSelected), Parcelable
