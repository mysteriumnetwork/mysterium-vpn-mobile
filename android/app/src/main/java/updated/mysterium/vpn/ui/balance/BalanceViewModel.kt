package updated.mysterium.vpn.ui.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import mysterium.GetBalanceRequest
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class BalanceViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val balanceLiveData: LiveData<Double>
        get() = _balanceLiveData

    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()
    private val deferredNode = DeferredNode()
    private val _balanceLiveData = MutableLiveData<Double>()
    private lateinit var balanceRequest: GetBalanceRequest

    fun initDeferredNode(mysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        viewModelScope.launch {
            startDeferredNode(mysteriumCoreService)
        }
    }

    fun getCurrentBalance() {
        viewModelScope.launch {
            _balanceLiveData.postValue(balanceUseCase.getBalance(balanceRequest))
        }
    }

    private suspend fun startDeferredNode(coreService: CompletableDeferred<MysteriumCoreService>) {
        coreService.await()
        if (!deferredNode.startedOrStarting()) {
            deferredNode.start(coreService.await())
        }
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

    private suspend fun initBalanceListener() {
        balanceUseCase.initBalanceListener {
            _balanceLiveData.postValue(it)
        }
    }
}
