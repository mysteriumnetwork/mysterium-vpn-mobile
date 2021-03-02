package updated.mysterium.vpn.network.usecase

import android.content.Context

class LoginUseCase(private val context: Context) {

    private companion object {
        const val LOGIN_PREFERENCES_KEY = "LOGIN"
        const val ALREADY_LOGIN_KEY = "ALREADY_LOGIN"
    }

    fun isAlreadyLogin(): Boolean {
        val loginPreferences = context.getSharedPreferences(
            LOGIN_PREFERENCES_KEY, Context.MODE_PRIVATE
        )
        return if (!loginPreferences.contains(ALREADY_LOGIN_KEY)) {
            loginPreferences.edit().putBoolean(ALREADY_LOGIN_KEY, true).apply()
            false
        } else {
            true
        }
    }
}
