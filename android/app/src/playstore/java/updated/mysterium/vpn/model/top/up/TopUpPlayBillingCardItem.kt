package updated.mysterium.vpn.model.top.up

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopUpPlayBillingCardItem(
    val id: String,
    val sku: String,
    override val amountUsd: Double,
    override var isSelected: Boolean = false
) : AmountUsdCardItem(amountUsd, isSelected), Parcelable
