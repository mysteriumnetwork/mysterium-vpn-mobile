package updated.mysterium.vpn.common.extensions

import kotlin.math.truncate

fun Float.toIntWithoutRounding() = if (this - truncate(this) == 0f) {
    this.toInt()
} else {
    -1
}
