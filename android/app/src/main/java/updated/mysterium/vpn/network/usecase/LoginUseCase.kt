package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class LoginUseCase(private val sharedPreferencesManager: SharedPreferencesManager) {

    fun isAlreadyLogin() = sharedPreferencesManager.isAlreadyLogin()

    fun userLoggedIn() = sharedPreferencesManager.userLoggedIn()
}
