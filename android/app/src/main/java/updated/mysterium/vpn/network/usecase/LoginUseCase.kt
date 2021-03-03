package updated.mysterium.vpn.network.usecase

import android.content.Context

class LoginUseCase(private val context: Context) {

    private companion object {
        const val LOGIN_PREFERENCES_KEY = "LOGIN"
        const val ALREADY_LOGIN_KEY = "ALREADY_LOGIN"
    }

    fun isAlreadyLogin() = context.getSharedPreferences(LOGIN_PREFERENCES_KEY, Context.MODE_PRIVATE)
        .contains(ALREADY_LOGIN_KEY)

    fun userLoggedIn() = context.getSharedPreferences(LOGIN_PREFERENCES_KEY, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(ALREADY_LOGIN_KEY, true).apply()
}
