package network.mysterium.ui

import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

enum class ConnState {
    NO_INTERNET,
    CONNECTED
}

// ConnectivityChecker for internet connection and exposes LiveData
// which can be used in activity or fragment.
class ConnectivityChecker: ViewModel() {
    val connState = MutableLiveData<ConnState>()

    private var job: Job? = null
    private val checkDurationMS = 2000L

    fun start(connectivityManager: ConnectivityManager) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

                viewModelScope.launch {
                    val newValue = if (isConnected) {
                        ConnState.CONNECTED
                    } else {
                        ConnState.NO_INTERNET
                    }
                    if (connState.value != newValue) {
                        connState.value = newValue
                    }
                }

                delay(checkDurationMS)
            }
        }
    }

    fun isConnected(): Boolean {
        return connState.value == ConnState.CONNECTED
    }

    fun stop() {
        job?.cancel()
    }
}
