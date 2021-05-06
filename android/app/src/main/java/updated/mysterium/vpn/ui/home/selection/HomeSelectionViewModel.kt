package updated.mysterium.vpn.ui.home.selection

import androidx.lifecycle.ViewModel
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.util.*

class HomeSelectionViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

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
        ConnectionState.valueOf(status.state.toUpperCase(Locale.ROOT))
    }
}
