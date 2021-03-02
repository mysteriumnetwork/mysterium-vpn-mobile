package updated.mysterium.vpn.ui.manual.connect.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import mysterium.ConnectRequest
import network.mysterium.AppNotificationManager
import network.mysterium.proposal.ProposalViewItem
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.service.core.Statistics
import network.mysterium.wallet.IdentityModel
import network.mysterium.wallet.IdentityRegistrationStatus
import updated.mysterium.vpn.common.extensions.liveDataResult
import updated.mysterium.vpn.model.manual.connect.ProposalModel
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider

class HomeViewModel(useCaseProvider: UseCaseProvider) : ViewModel() {

    val connectionState: LiveData<String>
        get() = _connectionState

    val statisticsUpdate: LiveData<Statistics>
        get() = _statisticsUpdate

    private lateinit var proposalModel: ProposalModel
    private lateinit var appNotificationManager: AppNotificationManager
    private lateinit var coreService: MysteriumCoreService
    private val _statisticsUpdate = MutableLiveData<Statistics>()
    private val _connectionState = MutableLiveData<String>()
    private val nodesUseCase = useCaseProvider.nodes()
    private val locationUseCase = useCaseProvider.location()
    private val connectionUseCase = useCaseProvider.connection()
    private val deferredNode = DeferredNode()
    private var identity: IdentityModel? = null
    private var isConnected = false

    fun connectNode(
        proposal: ProposalModel,
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>,
        notificationManager: AppNotificationManager
    ) = liveDataResult {
        proposalModel = proposal
        appNotificationManager = notificationManager
        coreService = deferredMysteriumCoreService.await()
        startDeferredNode()
        if (isConnected) {
            disconnectNode()
        }
        connect()
    }

    fun getLocation() = liveDataResult {
        locationUseCase.getLocation()
    }

    fun showStatisticsNotification(
        notificationTitle: String,
        notificationContent: String
    ) {
        viewModelScope.launch {
            appNotificationManager.showStatisticsNotification(notificationTitle, notificationContent)
        }
    }

    fun addToFavourite(proposal: ProposalModel) = liveDataResult {
        nodesUseCase.addToFavourite(proposal)
    }

    fun disconnect() {
        if (isConnected) {
            viewModelScope.launch {
                disconnectNode()
            }
        }
    }

    private suspend fun startDeferredNode() {
        if (!deferredNode.startedOrStarting()) {
            deferredNode.start(coreService)
        }
        connectionUseCase.initDeferredNode(deferredNode)
        locationUseCase.initDeferredNode(deferredNode)
        nodesUseCase.initDeferredNode(deferredNode)
        initListeners()
        loadIdentity()
    }

    private suspend fun initListeners() {
        connectionUseCase.registerStatisticsChangeCallback {
            _statisticsUpdate.postValue(it)
        }
        connectionUseCase.connectionStatusCallback {
            if (it == "NotConnected") {
                coreService.setActiveProposal(null)
                coreService.stopForeground()
                isConnected = false
            }
            _connectionState.postValue(it)
        }
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
            providerID = proposalModel.providerID
            serviceType = proposalModel.serviceType.type
        }
        connectionUseCase.connect(req)
        updateService()
        isConnected = true
    }

    private fun updateService() {
        coreService.setActiveProposal(
            ProposalViewItem(
                id = proposalModel.id,
                providerID = proposalModel.providerID,
                serviceType = proposalModel.serviceType,
                countryCode = proposalModel.countryCode,
                nodeType = proposalModel.nodeType,
                monitoringFailed = proposalModel.monitoringFailed,
                payment = proposalModel.payment
            )
        )
        coreService.startForegroundWithNotification(
            appNotificationManager.defaultNotificationID,
            appNotificationManager.createConnectedToVPNNotification()
        )
    }

    private suspend fun disconnectNode() {
        connectionUseCase.disconnect()
        coreService.setActiveProposal(null)
        coreService.stopForeground()
        isConnected = false
    }
}
