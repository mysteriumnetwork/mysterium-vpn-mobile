package updated.mysterium.vpn.ui.connection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import mysterium.ConnectRequest
import mysterium.GetBalanceRequest
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.ConnectionStatistic
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.model.notification.NotificationChannels
import updated.mysterium.vpn.model.proposal.parameters.ProposalViewItem
import updated.mysterium.vpn.model.statistics.Statistics
import updated.mysterium.vpn.model.statistics.StatisticsModel
import updated.mysterium.vpn.model.wallet.IdentityModel
import updated.mysterium.vpn.model.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import java.util.*

class ConnectionViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    private companion object {
        const val DEFAULT_DNS_OPTION = "auto"
        const val TAG = "HomeViewModel"
        const val SESSION_NUMBER_BEFORE_REVIEW = 3
    }

    val connectionState: LiveData<ConnectionState>
        get() = _connectionState

    val statisticsUpdate: LiveData<ConnectionStatistic>
        get() = _statisticsUpdate

    val connectionException: LiveData<Exception>
        get() = _connectionException

    val pushDisconnect: LiveData<Unit>
        get() = _pushDisconnect

    val manualDisconnect: LiveData<Unit>
        get() = _manualDisconnect

    var proposal: Proposal? = null
        private set
    var identity: IdentityModel? = null
        private set

    private lateinit var appNotificationManager: AppNotificationManager
    private var coreService: MysteriumCoreService? = null
    private val _connectionException = SingleLiveEvent<Exception>()
    private val _manualDisconnect = SingleLiveEvent<Unit>()
    private val _pushDisconnect = SingleLiveEvent<Unit>()
    private val _statisticsUpdate = MutableLiveData<ConnectionStatistic>()
    private val _connectionState = MutableLiveData<ConnectionState>()
    private val nodesUseCase = useCaseProvider.nodes()
    private val locationUseCase = useCaseProvider.location()
    private val connectionUseCase = useCaseProvider.connection()
    private val balanceUseCase = useCaseProvider.balance()
    private val settingsUseCase = useCaseProvider.settings()
    private val statisticUseCase = useCaseProvider.statistic()
    private val deferredNode = DeferredNode()
    private var exchangeRate: Double? = null
    private var isConnectionStopped = false

    fun init(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>,
        notificationManager: AppNotificationManager,
        proposal: Proposal
    ) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            appNotificationManager = notificationManager
            coreService = deferredMysteriumCoreService.await()
            startDeferredNode()
            connectNode(proposal)
        }
    }

    fun connectNode(proposal: Proposal) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i(TAG, exception.localizedMessage ?: exception.toString())
            if (!isConnectionStopped && _connectionState.value != ConnectionState.CONNECTED) {
                _connectionException.postValue(exception as Exception)
            }
            isConnectionStopped = false
        }
        viewModelScope.launch(handler) {
            this@ConnectionViewModel.proposal = proposal
            disconnectIfConnectedNode()
            connect()
            getExchangeRate()
        }
    }

    fun isReviewAvailable() = liveDataResult {
        val sessionsCount = statisticUseCase.getLastSessions().size
        if (sessionsCount == SESSION_NUMBER_BEFORE_REVIEW) {
            if (!settingsUseCase.isReviewShown()) {
                settingsUseCase.reviewShown()
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    fun getLocation() = liveDataResult {
        locationUseCase.getLocation()
    }

    fun addToFavourite(proposal: Proposal) = liveDataResult {
        nodesUseCase.addToFavourite(proposal)
    }

    fun deleteFromFavourite(proposal: Proposal) {
        viewModelScope.launch {
            nodesUseCase.deleteFromFavourite(proposal)
        }
    }

    fun disconnect() = liveDataResult {
        disconnectIfConnectedNode()
    }

    fun disconnectFromNotification() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        viewModelScope.launch(handler) {
            disconnectIfConnectedNode()
            _pushDisconnect.call()
        }
    }

    fun stopConnecting() = liveDataResult {
        isConnectionStopped = true
        disconnectNode()
    }

    fun updateCurrentConnectionStatus() = liveDataResult {
        val status = connectionUseCase.status()
        val connectionModel = ConnectionState.from(status.state)
        _connectionState.postValue(connectionModel)
        connectionModel
    }

    fun isFavourite(nodeId: String) = liveDataResult {
        nodesUseCase.isFavourite(nodeId)
    }

    fun manualDisconnect() {
        coreService?.manualDisconnect()
    }

    fun getBalance(identityAddress: String? = null) = liveDataResult {
        val balanceRequest = GetBalanceRequest().apply {
            this.identityAddress = identity?.address ?: identityAddress
        }
        balanceUseCase.getBalance(balanceRequest)
    }

    private suspend fun startDeferredNode() {
        if (!deferredNode.startedOrStarting()) {
            coreService?.let {
                deferredNode.start(it)
            }
        }
        connectionUseCase.initDeferredNode(deferredNode)
        locationUseCase.initDeferredNode(deferredNode)
        nodesUseCase.initDeferredNode(deferredNode)
        initListeners()
        loadIdentity()
    }

    private suspend fun initListeners() {
        connectionUseCase.registerStatisticsChangeCallback {
            updateStatistic(it)
        }
        connectionUseCase.connectionStatusCallback {
            val connectionStateModel = ConnectionState.from(it)
            if (connectionStateModel == ConnectionState.NOTCONNECTED) {
                coreService?.setDeferredNode(null)
                coreService?.setActiveProposal(null)
                coreService?.stopForeground()
            }
            _connectionState.postValue(connectionStateModel)
        }
    }

    private fun updateStatistic(statisticsCallback: Statistics) {
        val statistics = StatisticsModel.from(statisticsCallback)
        val connectionStatistic = ConnectionStatistic(
            duration = statistics.duration,
            bytesReceived = statistics.bytesReceived,
            bytesSent = statistics.bytesSent,
            tokensSpent = statistics.tokensSpent,
            currencySpent = (exchangeRate ?: 0.0) * statistics.tokensSpent
        )
        _statisticsUpdate.postValue(connectionStatistic)
    }

    private suspend fun loadIdentity() {
        connectionUseCase.getIdentity().let {
            identity = IdentityModel(
                it.address,
                it.channelAddress,
                IdentityRegistrationStatus.parse(it.registrationStatus)
            )
        }
    }

    private suspend fun connect() {
        val req = ConnectRequest().apply {
            identityAddress = identity?.address ?: ""
            providerID = proposal?.providerID
            serviceType = proposal?.serviceType?.type
            dnsOption = settingsUseCase.getSavedDns() ?: DEFAULT_DNS_OPTION
        }
        connectionUseCase.connect(req)
        updateService()
    }

    private suspend fun getExchangeRate() {
        exchangeRate = balanceUseCase.getUsdEquivalent()
    }

    private fun updateService() {
        coreService?.apply {
            proposal?.let {
                setActiveProposal(ProposalViewItem.parse(it))
            }
            setDeferredNode(deferredNode)
            startForegroundWithNotification(
                NotificationChannels.STATISTIC_NOTIFICATION,
                appNotificationManager.createConnectedToVPNNotification()
            )
            _connectionState.postValue(ConnectionState.CONNECTED)
        }
    }

    private suspend fun disconnectIfConnectedNode() {
        if (
            _connectionState.value == ConnectionState.CONNECTED ||
            _connectionState.value == ConnectionState.ON_HOLD ||
            _connectionState.value == ConnectionState.IP_NOT_CHANGED
        ) {
            disconnectNode()
        }
    }

    private suspend fun disconnectNode() {
        proposal = null
        coreService?.manualDisconnect()
        _manualDisconnect.call()
        connectionUseCase.disconnect()
        coreService?.setActiveProposal(null)
        coreService?.setDeferredNode(null)
        coreService?.stopForeground()
    }
}
