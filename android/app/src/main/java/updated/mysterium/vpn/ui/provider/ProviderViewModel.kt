package updated.mysterium.vpn.ui.provider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.ProviderState
import updated.mysterium.vpn.model.notification.NotificationChannels
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.notification.AppNotificationManager

data class ProviderService(
        var active1: Boolean,
        var active2: Boolean,
        var active3: Boolean,
)

class ProviderViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "ProviderViewModel"
    }


    val providerUpdate: LiveData<ProviderState>
        get() = _providerUpdate
    private val _providerUpdate = MutableLiveData<ProviderState>()


    private val servicesState = ProviderService(false,false,false)
    val providerServiceStatus: LiveData<ProviderService>
        get() = _providerServiceStatus
    private val _providerServiceStatus = MutableLiveData<ProviderService>()


    private var coreService: MysteriumCoreService? = null
    private val connectionUseCase = useCaseProvider.connection()
    private lateinit var appNotificationManager: AppNotificationManager

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.i(TAG, exception.localizedMessage ?: exception.toString())
    }

    fun init(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>,
        notificationManager: AppNotificationManager,
        ) {
        viewModelScope.launch(handler) {
            appNotificationManager = notificationManager
            coreService = deferredMysteriumCoreService.await()

            val initialState = ProviderState(
                active = getIsProvider(),
            )
            _providerUpdate.postValue(initialState)

            connectionUseCase.serviceStatusChangeCallback {
                val running = (it.status == "Running")
                when (it.service) {
                    "wireguard" -> servicesState.active1 = running
                    "data_transfer" -> servicesState.active2 = running
                    "scraping" -> servicesState.active3 = running
                }
                _providerServiceStatus.postValue(servicesState)
            }
        }
    }

    fun toggleProvider(isChecked: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            coreService?.let {
                if (isChecked) {
                    if (it.isProviderActive()) {
                        return@let
                    }

                    it.startProvider(true)
                    it.startForegroundWithNotification(
                            NotificationChannels.PROVIDER_NOTIFICATION,
                            appNotificationManager.createProviderNotification()
                    )
                } else {
                    if (!it.isProviderActive()) {
                        return@let
                    }

                    it.startProvider(false)
                    it.stopForeground()
                }
            }


        }
    }

    private suspend fun getIsProvider(): Boolean {
        coreService?.let {
            return it.isProviderActive()
        }
        return false
    }
}
