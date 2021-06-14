package updated.mysterium.vpn.ui.balance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import mysterium.GetBalanceRequest
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.model.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.connection.ConnectionViewModel

class BalanceViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val TAG = "BalanceViewModel"
    }

    val balanceLiveData: LiveData<Double>
        get() = _balanceLiveData

    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()
    private val deferredNode = DeferredNode()
    private val _balanceLiveData = MutableLiveData<Double>()
    private var balanceRequest: GetBalanceRequest? = null

    fun initDeferredNode(mysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            startDeferredNode(mysteriumCoreService)
        }
    }

    fun getCurrentBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            balanceRequest?.let {
                _balanceLiveData.postValue(balanceUseCase.getBalance(it))
            }
        }
    }

    private suspend fun startDeferredNode(coreService: CompletableDeferred<MysteriumCoreService>) {
        coreService.await().subscribeToListeners()
        if (!deferredNode.startedOrStarting()) {
            deferredNode.start(coreService.await())
        }
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            connectionUseCase.initDeferredNode(deferredNode)
            balanceUseCase.initDeferredNode(deferredNode)
            initBalanceListener()
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
    }

    private suspend fun initBalanceListener() {
        balanceUseCase.initBalanceListener {
            _balanceLiveData.postValue(it)
        }
    }
}
