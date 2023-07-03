package updated.mysterium.vpn.ui.home.selection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class HomeSelectionViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "HomeSelectionViewModel"
    }

    val connectionState: LiveData<ConnectionState>
        get() = _connectionState

    val isNewAppPopUpShow: LiveData<Boolean>
        get() = _isNewAppPopUpShow
    private val _isNewAppPopUpShow = MutableLiveData<Boolean>()

    val isNewAppNotificationShow: LiveData<Boolean>
        get() = _isNewAppNotificationShow
    private val _isNewAppNotificationShow = MutableLiveData<Boolean>()

    val showNewAppUrl = SingleLiveEvent<String>()

    private val _connectionState = MutableLiveData<ConnectionState>()
    private val connectionUseCase = useCaseProvider.connection()
    private val locationUseCase = useCaseProvider.location()
    private val filtersUseCase = useCaseProvider.filters()
    private val settingsUseCase = useCaseProvider.settings()
    private val newPopupUseCase = useCaseProvider.newAppPopup()

    init {
        refreshNewAppShowState(NewAppPopupSource.POP_UP)
        refreshNewAppShowState(NewAppPopupSource.NOTIFICATION)
    }

    fun getLocation() = liveDataResult {
        locationUseCase.getLocation()
    }

    fun getSystemPresets() = liveDataResult {
        filtersUseCase.getSystemPresets()
    }

    fun getCurrentState() = liveDataResult {
        connectionUseCase.status().state
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

    fun saveNewCountryCode(countryCode: String) {
        filtersUseCase.saveNewCountryCode(countryCode)
    }

    fun getPreviousCountryCode() = filtersUseCase.getPreviousCountryCode()

    fun saveNewFilterId(filterId: Int?) {
        filtersUseCase.saveNewFilterId(filterId ?: 1)
    }

    fun getPreviousFilterId() = filtersUseCase.getPreviousFilterId()

    fun getResidentCountry() = liveDataResult {
        settingsUseCase.getResidentCountry()
    }

    fun openNewAppLink(popUp: NewAppPopupSource, link: String) {
        showNewAppUrl.value = link
        closeNewAppPopups(popUp)
    }

    fun closeNewAppPopups(popUp: NewAppPopupSource) {
        newPopupUseCase.disablePopUp(popUp)
        refreshNewAppShowState(popUp)
    }

    private fun refreshNewAppShowState(popUp: NewAppPopupSource) {
        val result = newPopupUseCase.getPopUpStatus(popUp)
        when (popUp) {
            NewAppPopupSource.POP_UP -> _isNewAppPopUpShow.value = result
            NewAppPopupSource.NOTIFICATION -> _isNewAppNotificationShow.value = result
        }
    }
}
