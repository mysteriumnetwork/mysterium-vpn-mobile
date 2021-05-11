package updated.mysterium.vpn.common.extensions

import android.graphics.RectF
import android.view.View
import androidx.annotation.StringRes
import network.mysterium.proposal.NodeType
import network.mysterium.vpn.R


@StringRes
fun NodeType.getTypeLabelResource(): Int = when (this) {
    NodeType.RESIDENTIAL -> R.string.manual_connect_residential
    NodeType.CELLULAR -> R.string.manual_connect_residential
    else -> R.string.manual_connect_non_residential
}

fun View.calculateRectOnScreen(): RectF {
    val location = IntArray(2) // get view x;y location coordinates on screen
    this.getLocationOnScreen(location)
    return RectF(
        location[0].toFloat(),
        location[1].toFloat(),
        location[0].toFloat() + this.measuredWidth,
        location[1].toFloat() + this.measuredHeight
    )
}
