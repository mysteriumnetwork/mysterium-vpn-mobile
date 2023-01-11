package updated.mysterium.vpn.ui.balance

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mysterium.GetBalanceRequest
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.model.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class BalanceViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val REQUEST_INTERVAL = 1000 * 60L // 1 minute
    }

    val balanceLiveData: LiveData<Double>
        get() = _balanceLiveData

    val paymentReceivedLiveData: LiveData<Double>
        get() = _paymentReceivedLiveData

    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()
    private val _balanceLiveData = MutableLiveData<Double>()
    private val _paymentReceivedLiveData = MutableLiveData<Double>()
    private var balanceRequest: GetBalanceRequest? = null
    private var initialBalance: Double? = null

    private val handler = Handler()
    private val runnable = object : Runnable {

        override fun run() {
            try {
                forceBalanceUpdate()
            } catch (exception: Exception) {
                Log.e(TAG, exception.localizedMessage ?: exception.toString())
            } finally {
                handler.postDelayed(this, REQUEST_INTERVAL)
            }
        }
    }

    fun initDeferredNode(mysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            startDeferredNode(mysteriumCoreService)
        }
    }

    fun launchForceBalanceUpdatePeriodically() {
        // fetch new proposals list every 1 min
        handler.post(runnable)
    }

    fun stopForceBalanceUpdatePeriodically() {
        handler.removeCallbacks(runnable)
    }

    fun requestBalanceChange() {
        viewModelScope.launch(Dispatchers.IO) {
            if (balanceRequest == null) {
                val handler = CoroutineExceptionHandler { _, exception ->
                    Log.e(TAG, exception.localizedMessage ?: exception.toString())
                }
                viewModelScope.launch(Dispatchers.IO + handler) {
                    initBalanceRequest()
                }
            }
            balanceRequest?.let {
                _balanceLiveData.postValue(balanceUseCase.getBalance(it))
            }
        }
    }

    fun forceBalanceUpdate() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            if (balanceRequest == null) {
                initBalanceRequest()
            }
            balanceRequest?.let {
                val balance = balanceUseCase.forceBalanceUpdate(it).balance
                _balanceLiveData.postValue(balance)
                initialBalance?.let { initialBalance ->
                    if (balance > initialBalance) {
                        _paymentReceivedLiveData.postValue(balance - initialBalance)
                    }
                }
                initialBalance = balance
            }
        }
    }

    private suspend fun startDeferredNode(coreService: CompletableDeferred<MysteriumCoreService>) {
        coreService.await().subscribeToListeners()
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            initBalanceListener()
            initBalanceRequest()
        }
    }

    private suspend fun initBalanceRequest() {
        val nodeIdentity = connectionUseCase.getIdentity()
        val identity = IdentityModel(
            address = nodeIdentity.address,
            channelAddress = nodeIdentity.channelAddress,
            status = IdentityRegistrationStatus.parse(nodeIdentity.registrationStatus)
        )
        balanceRequest = GetBalanceRequest().apply {
            identityAddress = identity.address
        }
    }

    private suspend fun initBalanceListener() {
        balanceUseCase.initBalanceListener {
            _balanceLiveData.postValue(it)
        }
    }
}
