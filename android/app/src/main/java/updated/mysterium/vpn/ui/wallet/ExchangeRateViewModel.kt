package updated.mysterium.vpn.ui.wallet

import android.os.Handler
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class ExchangeRateViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val REQUEST_INTERVAL = 1000 * 60 * 15L // 15 minutes
        const val TAG = "ExchangeRateViewModel"
    }

    var usdEquivalent = 0.0
        private set

    private val balanceUseCase = useCaseProvider.balance()
    private val handler = Handler()
    private val runnable = object : Runnable {

        override fun run() {
            try {
                loadRate()
            } catch (exception: Exception) {
                Log.e(TAG, exception.localizedMessage ?: exception.toString())
            } finally {
                handler.postDelayed(this, REQUEST_INTERVAL)
            }
        }
    }

    fun launchPeriodicallyExchangeRate() {
        // fetch new rate every 15 min
        handler.post(runnable)
    }

    fun stopPeriodicallyExchangeRate() {
        handler.removeCallbacks(runnable)
    }

    fun loadRate() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            usdEquivalent = balanceUseCase.getUsdEquivalent()
        }
    }

    fun getMystEquivalent(price: Double): Double {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        var chfEquivalent = 0.0
        viewModelScope.launch(Dispatchers.IO + handler) {
            chfEquivalent = balanceUseCase.getChfEquivalent()
        }
        return price * chfEquivalent
    }

}
