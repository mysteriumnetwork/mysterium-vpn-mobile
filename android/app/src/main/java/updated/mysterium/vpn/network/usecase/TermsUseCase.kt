package updated.mysterium.vpn.network.usecase

import network.mysterium.terms.Terms
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class TermsUseCase(private val sharedPreferencesManager: SharedPreferencesManager) {

    val fullTermsData by lazy {
        Terms.endUserMD()
    }

    fun isTermsUpdated(): Boolean {
        val userAcceptedVersion = sharedPreferencesManager.getStringPreferenceValue(
            SharedPreferencesList.TERMS
        )
        return (userAcceptedVersion != null && userAcceptedVersion != Terms.version())
    }

    fun isTermsAccepted(): Boolean {
        val userAcceptedVersion = sharedPreferencesManager.getStringPreferenceValue(
            SharedPreferencesList.TERMS
        )
        return (userAcceptedVersion == Terms.version())
    }

    fun userAcceptTerms() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.TERMS,
        value = Terms.version()
    )

}
