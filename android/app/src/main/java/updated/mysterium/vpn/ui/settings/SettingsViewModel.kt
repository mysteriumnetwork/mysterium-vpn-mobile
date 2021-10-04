package updated.mysterium.vpn.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.connection.ConnectionViewModel

class SettingsViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "SettingsViewModel"
    }

    private val settingsUseCase = useCaseProvider.settings()
    private val connectionUseCase = useCaseProvider.connection()

    fun saveDnsOption(dnsOption: String) {
        settingsUseCase.saveDns(dnsOption)
    }

    fun getSavedDnsOption() = settingsUseCase.getSavedDns()

    fun saveResidentCountry(countryCode: String) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            val identityAddress = connectionUseCase.getIdentityAddress()
            settingsUseCase.saveResidentCountry(identityAddress, countryCode)
        }
    }

    fun getResidentCountry() = liveDataResult {
        settingsUseCase.getResidentCountry()
    }

    fun changeLightMode(isDark: Boolean) {
        settingsUseCase.setUserDarkMode(isDark)
    }

    fun setNetOption(isNetAvailable: Boolean) {
        settingsUseCase.setNatCompatibility(isNetAvailable)
    }

    fun isNetCompatibilityAvailable() = settingsUseCase.isNatAvailable()
}
