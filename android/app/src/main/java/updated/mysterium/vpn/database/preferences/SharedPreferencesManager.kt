package updated.mysterium.vpn.database.preferences

import android.content.Context

class SharedPreferencesManager(private val context: Context) {

    private companion object {
        const val BALANCE_POP_UP_PREFERENCES_KEY = "BALANCE_POP_UP"
        const val BALANCE_POP_UP_SHOWN_KEY = "BALANCE_POP_UP_SHOWN"
        const val LOGIN_PREFERENCES_KEY = "LOGIN"
        const val ALREADY_LOGIN_KEY = "ALREADY_LOGIN"
        const val TERMS_PREFERENCES_KEY = "TERMS"
        const val ALREADY_ACCEPTED_KEY = "ALREADY_ACCEPTED"
    }

    fun isBalancePopUpShown() = context.getSharedPreferences(
        BALANCE_POP_UP_PREFERENCES_KEY, Context.MODE_PRIVATE
    ).contains(BALANCE_POP_UP_SHOWN_KEY)

    fun balancePopUpShown() = context
        .getSharedPreferences(BALANCE_POP_UP_PREFERENCES_KEY, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(BALANCE_POP_UP_SHOWN_KEY, true).apply()

    fun isAlreadyLogin() = context.getSharedPreferences(LOGIN_PREFERENCES_KEY, Context.MODE_PRIVATE)
        .contains(ALREADY_LOGIN_KEY)

    fun userLoggedIn() = context.getSharedPreferences(LOGIN_PREFERENCES_KEY, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(ALREADY_LOGIN_KEY, true).apply()

    fun isTermsAccepted() = context.getSharedPreferences(TERMS_PREFERENCES_KEY, Context.MODE_PRIVATE)
        .contains(ALREADY_ACCEPTED_KEY)

    fun userAcceptTerms() = context.getSharedPreferences(TERMS_PREFERENCES_KEY, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(ALREADY_ACCEPTED_KEY, true).apply()
}
