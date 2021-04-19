package updated.mysterium.vpn.ui.menu

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.languages.LanguagesUtil
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class MenuViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val settingsUseCase = useCaseProvider.settings()

    fun getUserLanguageIndex(): Int {
        val language = settingsUseCase.getUserSelectedLanguage()
        return LanguagesUtil.getIndexByCountry(language)
    }

    fun saveUserSelectedLanguage(index: Int) {
        val countryCode = LanguagesUtil.getCountryCodeByIndex(index)
        settingsUseCase.userNewCountryLanguage(countryCode)
    }
}
