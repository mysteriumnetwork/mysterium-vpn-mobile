package updated.mysterium.vpn.network.usecase

import updated.mysterium.vpn.database.preferences.SharedPreferencesList
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.ui.home.selection.NewAppPopupSource

class NewAppPopupUseCase(
    private val sharedPreferencesManager: SharedPreferencesManager
) {

    fun getPopUpStatus(source: NewAppPopupSource): Boolean =
        sharedPreferencesManager.getBoolPreferenceValue(mapTypes(source), true)

    fun disablePopUp(source: NewAppPopupSource) =
        sharedPreferencesManager.setPreferenceValue(mapTypes(source), false)

    private fun mapTypes(source: NewAppPopupSource): SharedPreferencesList = when (source) {
        NewAppPopupSource.POP_UP -> SharedPreferencesList.IS_POP_UP_TO_SHOW
        NewAppPopupSource.NOTIFICATION -> SharedPreferencesList.IS_NOTIFICATION_TO_SHOW
    }

}
