package updated.mysterium.vpn.ui.provider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.model.manual.connect.ProviderState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class ProviderViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "ProviderViewModel"
    }

    val providerUpdate: LiveData<ProviderState>
        get() = _providerUpdate

    private val _providerUpdate = MutableLiveData<ProviderState>()
    private var coreService: MysteriumCoreService? = null

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.i(TAG, exception.localizedMessage ?: exception.toString())
    }

    fun init(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>,
    ) {
        println("ProviderViewModel > init")
        viewModelScope.launch(handler) {
            coreService = deferredMysteriumCoreService.await()

            val initialState = ProviderState(
                active = getIsProvider(),
            )
            _providerUpdate.postValue(initialState)
        }
    }

    fun toggleProvider(isChecked: Boolean) {
        println ("MYDBG >>>>>>>>>>>> toggleProvider ! $isChecked")

        // which scope is correct ?
        CoroutineScope(Dispatchers.IO).launch {
            coreService?.let {
                if (isChecked) {
                    if (it.isProviderActive()) {
                        return@let
                    }

                    // stop consumer
                    it.stopConsumer()
                    it.startProvider(true)

                } else {
                    if (!it.isProviderActive()) {
                        return@let
                    }
                    it.startProvider(false)
                }
            }
        }
    }

    suspend fun getIsProvider(): Boolean {
        coreService?.let {
            return it.isProviderActive()
        }
        return false
    }
}
