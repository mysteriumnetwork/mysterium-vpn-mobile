package updated.mysterium.vpn.ui.home.selection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.network.usecase.NodesUseCase

class HomeSelectionViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "HomeSelectionViewModel"
    }

    val connectionState: LiveData<ConnectionState>
        get() = _connectionState

    var countryCode: String = NodesUseCase.ALL_COUNTRY_CODE
    var filterId: Int? = null
    private val _connectionState = MutableLiveData<ConnectionState>()
    private val connectionUseCase = useCaseProvider.connection()
    private val locationUseCase = useCaseProvider.location()
    private val filtersUseCase = useCaseProvider.filters()

    fun getLocation() = liveDataResult {
        locationUseCase.getLocation()
    }

    fun getSystemPresets() = liveDataResult {
        filtersUseCase.getSystemPresets()
    }

    fun getCurrentState() = liveDataResult {
        val status = connectionUseCase.status()
        ConnectionState.from(status.state)
    }

    fun initConnectionListener() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            connectionUseCase.connectionStatusCallback {
                val state = ConnectionState.from(it)
                _connectionState.postValue(state)
            }
        }
    }
}
