package updated.mysterium.vpn.ui.provider

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class ProviderViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "ProviderViewModel"
    }

    private var deferredNode = DeferredNode()
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
        }
    }

    fun toggleProvider(isChecked: Boolean) {

        viewModelScope.launch(handler) {
            coreService?.let {

                if (isChecked) {
                    println ("MYDBG >>>>>>>>>>>> connect ! ")

                    it.setProviderActive(true)
                    deferredNode.start(it, true)
                } else {
                    println ("MYDBG >>>>>>>>>>>> disconnect ! ")

                    // disconnect
                    it.stopNode()
                    it.setProviderActive(false)
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
