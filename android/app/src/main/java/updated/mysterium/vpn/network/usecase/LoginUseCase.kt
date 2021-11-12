package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager

class LoginUseCase(
    private val nodeRepository: NodeRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    suspend fun isFreeRegistrationAvailable(
        address: String
    ) = nodeRepository.isFreeRegistrationEligible(address)

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

    fun isFreeRegistrationAvailable() = sharedPreferencesManager.getBoolPreferenceValue(
        SharedPreferencesList.ACCOUNT_CREATED,
        false
    )

    fun accountCreated() = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.ACCOUNT_CREATED,
        value = true
    )

    fun setRegistrationAbility(isAvailable: Boolean) {
        sharedPreferencesManager.setPreferenceValue(
            key = SharedPreferencesList.FREE_REGISTRATION,
            value = isAvailable
        )
    }

    fun userCreateOrImportAccount(isNewUser: Boolean) = sharedPreferencesManager.setPreferenceValue(
        key = SharedPreferencesList.IS_NEW_USER,
        value = isNewUser
    )

    fun isNewUser() = sharedPreferencesManager.getBoolPreferenceValue(
        SharedPreferencesList.IS_NEW_USER
    )
}
