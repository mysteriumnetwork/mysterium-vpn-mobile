package updated.mysterium.vpn.common.extensions

fun String.isEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword() = this.length in 12..16
