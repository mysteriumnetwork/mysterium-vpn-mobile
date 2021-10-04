package updated.mysterium.vpn.common.extensions

import android.content.res.Resources
import kotlin.math.truncate

fun Float.toIntWithoutRounding() = if (this - truncate(this) == 0f) {
    this.toInt()
} else {
    -1
}

val Float.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
