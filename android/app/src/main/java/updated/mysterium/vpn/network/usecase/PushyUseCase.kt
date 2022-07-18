package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class PushyUseCase(private val sharedPreferencesManager: SharedPreferencesManager) {

    fun getCryptoCurrency() = sharedPreferencesManager.getStringPreferenceValue(
        SharedPreferencesList.CRYPTO_PAYMENT
    )

    fun updateCryptoCurrency(currency: String) {
        sharedPreferencesManager.setPreferenceValue(
            SharedPreferencesList.CRYPTO_PAYMENT, currency
        )
    }
}
