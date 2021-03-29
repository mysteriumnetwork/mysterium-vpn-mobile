package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class SettingsUseCase(private val sharedPreferencesManager: SharedPreferencesManager) {

    fun getSavedDns() = sharedPreferencesManager.getStringPreferenceValue(SharedPreferencesList.DNS)

    fun saveDns(dnsOption: String) = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.DNS,
        value = dnsOption
    )
}
