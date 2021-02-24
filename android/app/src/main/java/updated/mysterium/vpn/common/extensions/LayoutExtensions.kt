package updated.mysterium.vpn.common.extensions

import android.content.Context
import android.util.TypedValue

fun Context.dpToPx(dp: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp / 2,
    resources.displayMetrics
).toInt()
