package updated.mysterium.vpn.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.service.core.NodeRepository
import updated.mysterium.vpn.database.entity.NodeEntity
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import java.util.*
import kotlin.concurrent.timerTask

class SplashViewModel(
    useCaseProvider: UseCaseProvider,
    private val nodeRepository: NodeRepository
) : ViewModel() {

    private val nodesUseCase = useCaseProvider.nodes()
    private var cachedNodesList: List<NodeEntity> = emptyList()
    private var isTimerFinished = false
    private var isDataLoaded = false
    private var _navigateToOnboarding = MutableLiveData<String>()
    val navigateToOnboarding
        get() = _navigateToOnboarding

    private val deferredNode = DeferredNode()

    fun startLoading(deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        startTimer()
        viewModelScope.launch {
            startDeferredNode(deferredMysteriumCoreService)
            loadData()
        }
    }

    private fun startDeferredNode(deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        CoroutineScope(Dispatchers.Main).launch {
            deferredMysteriumCoreService.await()
            if (!deferredNode.startedOrStarting()) {
                deferredNode.start(deferredMysteriumCoreService.await())
            }
        }
        nodeRepository.deferredNode = deferredNode
    }

    private fun startTimer() {
        Timer().schedule(timerTask {
            if (isDataLoaded) {
                _navigateToOnboarding.postValue("")
            } else {
                isTimerFinished = true
            }
        }, ONE_SECOND_DELAY)
    }

    private suspend fun loadData() {
        cachedNodesList = nodesUseCase.getAllInitialNodes()
        if (isTimerFinished) {
            nodesUseCase.saveAllInitialNodes(cachedNodesList)
            _navigateToOnboarding.postValue("")
        } else {
            isDataLoaded = true
        }
    }

    companion object {
        private const val ONE_SECOND_DELAY = 1000L
    }
}
