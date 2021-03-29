package updated.mysterium.vpn.ui.settings

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SettingsViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val settingsUseCase = useCaseProvider.settings()

    fun saveDnsOption(dnsOption: String) {
        settingsUseCase.saveDns(dnsOption)
    }

    fun getSavedDnsOption() = settingsUseCase.getSavedDns()
}
