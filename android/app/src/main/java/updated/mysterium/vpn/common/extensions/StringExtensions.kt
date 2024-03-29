package updated.mysterium.vpn.common.extensions

fun String.isEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword() = this.length >= 12

fun String.isValidRegistrationToken() = this.length > 3

fun String.getPushySubcategoryName() = "android.$this"

fun String.guaranteeCorrectLink(): String {
    return if (!startsWith("http://") && !startsWith("https://")) {
        "http://$this"
    } else {
        this
    }
}
