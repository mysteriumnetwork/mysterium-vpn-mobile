package updated.mysterium.vpn.ui.provider

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import okhttp3.internal.wait
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class ProviderViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "ProviderViewModel"
    }

    private val settingsUseCase = useCaseProvider.settings()
    private val providerUseCase = useCaseProvider.connection()
    private var deferredNode = DeferredNode()
    private var coreService: MysteriumCoreService? = null
    val handler = CoroutineExceptionHandler { _, exception ->
        Log.i(TAG, exception.localizedMessage ?: exception.toString())
//        if (!isConnectionStopped && _connectionStatus.value?.state != ConnectionState.CONNECTED) {
//            _connectionException.postValue(exception as Exception)
//        }
//        isConnectionStopped = false
    }

    fun init(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>,
    ) {
        viewModelScope.launch(handler) {
//            appNotificationManager = notificationManager
            coreService = deferredMysteriumCoreService.await()
//            startDeferredNode()
//            connect(connectionType, countryCode, proposal, rate)
        }
    }

    fun toggleProvider(isCheccked: Boolean) {
        Log.d(TAG, "isChecked >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println(deferredNode.startedOrStarting())

//        if (!deferredNode.startedOrStarting()) {
            viewModelScope.launch(handler) {
                coreService?.let {

                    var a = getIsProviderActive_()
                    if (!a && isCheccked) {

                        providerUseCase.disconnect()
//                        providerUseCase.connect()
//                        deferredNode.await().disconnect()
//                        deferredNode.wait()
//                        deferredNode.start(it)

                    }



                }
            }

//        }
        //settingsUseCase.setUserDarkMode(isDark)
    }

    fun saveDnsOption(dnsOption: String) {
        //settingsUseCase.saveDns(dnsOption)
    }

//    fun getSavedDnsOption() = settingsUseCase.getSavedDns()
//
//    fun saveResidentCountry(countryCode: String) {
//        val handler = CoroutineExceptionHandler { _, exception ->
//            Log.e(TAG, exception.localizedMessage ?: exception.toString())
//        }
//        viewModelScope.launch(handler) {
//            val identityAddress = connectionUseCase.getIdentityAddress()
//            settingsUseCase.saveResidentCountry(identityAddress, countryCode)
//        }
//    }

//    fun getResidentCountry() = liveDataResult {
//        settingsUseCase.getResidentCountry()
//    }
//
//    fun changeLightMode(isDark: Boolean) {
//        settingsUseCase.setUserDarkMode(isDark)
//    }

    fun setNatOption(isNatAvailable: Boolean) {
        settingsUseCase.setNatCompatibility(isNatAvailable)
    }

    fun isNatCompatibilityAvailable() = settingsUseCase.isNatAvailable()

    suspend fun getIsProviderActive_(): Boolean {
        return providerUseCase.getIsProviderActive()
    }


}
