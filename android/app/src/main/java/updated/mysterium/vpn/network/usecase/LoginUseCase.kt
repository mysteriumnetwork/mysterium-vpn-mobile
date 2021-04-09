package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class LoginUseCase(private val sharedPreferencesManager: SharedPreferencesManager) {

    fun isAlreadyLogin() = sharedPreferencesManager.containsPreferenceValue(SharedPreferencesList.LOGIN)

    fun userLoggedIn() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.LOGIN,
        value = true
    )

    fun isAccountFlowShown() = sharedPreferencesManager.containsPreferenceValue(SharedPreferencesList.ACCOUNT)

    fun accountFlowShown() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.ACCOUNT,
        value = true
    )
}
