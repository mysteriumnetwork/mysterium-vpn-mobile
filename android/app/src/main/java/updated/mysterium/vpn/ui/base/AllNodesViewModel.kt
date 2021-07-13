package updated.mysterium.vpn.ui.base

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import updated.mysterium.vpn.model.manual.connect.CountryNodes
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class AllNodesViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "AllNodesViewModel"
        const val REQUEST_INTERVAL = 1000 * 60L // 1 minute
    }

    val proposals: LiveData<List<CountryNodes>>
        get() = _proposals

    private val _proposals = MutableLiveData<List<CountryNodes>>()
    private val nodesUseCase = useCaseProvider.nodes()
    private var cachedNodesList: List<CountryNodes> = emptyList()

    fun launchProposalsPeriodically() {
        val handler = Handler()
        val runnable = object : Runnable {

            override fun run() {
                try {
                    getProposals()
                } catch (exception: Exception) {
                    Log.e(TAG, exception.localizedMessage ?: exception.toString())
                } finally {
                    handler.postDelayed(this, REQUEST_INTERVAL)
                }
            }
        }
        handler.postDelayed(runnable, REQUEST_INTERVAL)
    }

    fun getProposals(isReload: Boolean = false) {
        Log.e(TAG, "getProposals")
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
            if (!isReload) { // try to re load list only one time
                getProposals(isReload = true)
            }
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            updateLiveData()
            cachedNodesList = nodesUseCase.getAllCountries()
            updateLiveData()
        }
    }

    private fun updateLiveData() {
        if (cachedNodesList.isNotEmpty()) {
            _proposals.postValue(cachedNodesList)
        }
    }
}
