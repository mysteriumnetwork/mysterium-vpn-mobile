package updated.mysterium.vpn.network.usecase

import mysterium.ResidentCountryUpdateRequest
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.common.languages.LanguagesUtil
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class SettingsUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    fun isConnectionHintShown() = sharedPreferencesManager.containsPreferenceValue(
        SharedPreferencesList.CONNECTION_HINT
    )

    fun connectionHintShown() {
        sharedPreferencesManager.setPreferenceValue(
            SharedPreferencesList.CONNECTION_HINT,
            true
        )
    }

    fun userInitialCountryLanguage(countryCode: String) = if (getUserSelectedLanguage() == null) {
        sharedPreferencesManager.setPreferenceValue(
            key = SharedPreferencesList.LANGUAGE,
            value = countryCode
        )
        countryCode
    } else {
        getUserSelectedLanguage() ?: LanguagesUtil.getUserDefaultLanguage()
    }

    fun userNewCountryLanguage(countryCode: String) {
        sharedPreferencesManager.setPreferenceValue(
            key = SharedPreferencesList.LANGUAGE,
            value = countryCode
        )
    }

    fun getUserSelectedLanguage() = sharedPreferencesManager.getStringPreferenceValue(
        SharedPreferencesList.LANGUAGE
    )

    fun getSavedDns() = sharedPreferencesManager.getStringPreferenceValue(SharedPreferencesList.DNS)

    fun saveDns(dnsOption: String) = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.DNS,
        value = dnsOption
    )

    fun getUserDarkMode(): Boolean? {
        val isUserChoose = sharedPreferencesManager.containsPreferenceValue(
            SharedPreferencesList.DARK_MODE
        )
        return if (isUserChoose) {
            sharedPreferencesManager.getBoolPreferenceValue(
                SharedPreferencesList.DARK_MODE
            )
        } else {
            null
        }
    }

    fun setUserDarkMode(isDark: Boolean) {
        sharedPreferencesManager.setPreferenceValue(
            SharedPreferencesList.DARK_MODE, isDark
        )
    }

    suspend fun getResidentCountry() = nodeRepository.getResidentCountry()

    suspend fun saveResidentCountry(identityAddress: String, countryCode: String) {
        val residentCountryUpdateRequest = ResidentCountryUpdateRequest().apply {
            this.country = countryCode
            this.identityAddress = identityAddress
        }
        nodeRepository.saveResidentCountry(residentCountryUpdateRequest)
    }
}
