package updated.mysterium.vpn.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class SettingsViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private val settingsUseCase = useCaseProvider.settings()
    private val connectionUseCase = useCaseProvider.connection()

    fun saveDnsOption(dnsOption: String) {
        settingsUseCase.saveDns(dnsOption)
    }

    fun getSavedDnsOption() = settingsUseCase.getSavedDns()

    fun saveResidentCountry(countryCode: String) {
        viewModelScope.launch {
            val identityAddress = connectionUseCase.getIdentityAddress()
            settingsUseCase.saveResidentCountry(identityAddress, countryCode)
        }
    }

    fun getResidentCountry() = liveDataResult {
        settingsUseCase.getResidentCountry()
    }
}
