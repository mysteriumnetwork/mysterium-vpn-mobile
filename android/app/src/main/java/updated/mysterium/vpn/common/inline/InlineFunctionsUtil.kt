package updated.mysterium.vpn.common.inline

inline fun <reified T : Enum<T>> safeValueOf(type: String): T? = try {
    java.lang.Enum.valueOf(T::class.java, type)
} catch (e: IllegalArgumentException) {
    null
}
