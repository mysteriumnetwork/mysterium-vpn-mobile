package updated.mysterium.vpn.ui.menu

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.languages.LanguagesUtil
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class MenuViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val settingsUseCase = useCaseProvider.settings()
    private var isFirstLanguageChanging: Boolean? = null

    fun isFirstLanguageChanging() = isFirstLanguageChanging == true

    fun userManualSelect() {
        if (isFirstLanguageChanging == null) {
            isFirstLanguageChanging = true
        } else if (isFirstLanguageChanging == true) {
            isFirstLanguageChanging = false
        }
    }

    fun getUserLanguageCode() = settingsUseCase.getUserSelectedLanguage()

    fun getUserLanguageIndex(): Int {
        val language = settingsUseCase.getUserSelectedLanguage()
        return LanguagesUtil.getIndexByCountry(language)
    }

    fun saveUserSelectedLanguage(index: Int) {
        val countryCode = LanguagesUtil.getCountryCodeByIndex(index)
        settingsUseCase.userNewCountryLanguage(countryCode)
    }
}
