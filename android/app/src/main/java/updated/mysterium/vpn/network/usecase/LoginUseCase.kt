package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class LoginUseCase(private val sharedPreferencesManager: SharedPreferencesManager) {

    fun isAlreadyLogin() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.LOGIN
    )

    fun userLoggedIn() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.LOGIN,
        value = true
    )

    fun isTopFlowShown() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.TOP_UP_FLOW
    )

    fun accountFlowShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.TOP_UP_FLOW,
        value = true
    )

    fun isAccountCreated() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.ACCOUNT_CREATED
    )

    fun accountCreated() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.ACCOUNT_CREATED,
        value = true
    )
}
