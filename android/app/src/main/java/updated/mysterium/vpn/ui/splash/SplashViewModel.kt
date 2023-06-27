package updated.mysterium.vpn.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.livedata.SingleLiveEvent
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.notification.ReviveUserWork
import java.util.concurrent.TimeUnit

class SplashViewModel(
    useCaseProvider: UseCaseProvider,
    private val workManager: WorkManager
) : ViewModel() {

    val navigateForward: LiveData<Unit>
        get() = _navigateForward

    val preloadFinished: LiveData<Unit>
        get() = _preloadFinished

    val nodeStartingError: LiveData<Exception>
        get() = _nodeStartingError

    private val _nodeStartingError = MutableLiveData<Exception>()
    private val _preloadFinished = SingleLiveEvent<Unit>()
    private val _navigateForward = MutableLiveData<Unit>()
    private val balanceUseCase = useCaseProvider.balance()
    private val connectionUseCase = useCaseProvider.connection()
    private val loginUseCase = useCaseProvider.login()
    private val termsUseCase = useCaseProvider.terms()
    private val settingsUseCase = useCaseProvider.settings()
    private var deferredNode = DeferredNode()
    private var service: MysteriumCoreService? = null
    private var isNavigateForward = false

    fun startLoading(
        deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>
    ) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            _nodeStartingError.postValue(exception as Exception?)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            service = deferredMysteriumCoreService.await()
            if (
                service?.getDeferredNode() != null &&
                service?.getDeferredNode()?.startedOrStarting() == true
            ) {
                service?.getDeferredNode()?.let {
                    deferredNode = it
                    _preloadFinished.postValue(Unit)
                }
            } else {
                service?.let {
                    deferredNode.start(it) { exception ->
                        if (exception != null) {
                            _nodeStartingError.postValue(exception)
                        } else {
                            _preloadFinished.postValue(Unit)
                        }
                    }
                }
            }
        }
    }

    fun isUserAlreadyLogin() = loginUseCase.isAlreadyLogin()

    fun isTermsAccepted() = termsUseCase.isTermsAccepted()

    fun isAccountCreated() = loginUseCase.isAccountCreated()

    fun isTopUpFlowShown() = loginUseCase.isTopFlowShown()

    fun initRepository() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
            _nodeStartingError.postValue(exception as Exception?)
        }
        viewModelScope.launch(Dispatchers.IO + handler) {
            service?.subscribeToListeners()
            balanceUseCase.initDeferredNode(deferredNode)
            connectionUseCase.initDeferredNode(deferredNode)
            if (!isNavigateForward) {
                isNavigateForward = true
                _navigateForward.postValue(Unit)
            }
        }
    }

    fun getUserSavedMode() = settingsUseCase.getUserDarkMode()

    fun setUpInactiveUserPushyNotifications() {
        val firstNotificationWork =
            OneTimeWorkRequest
                .Builder(ReviveUserWork::class.java)
                .setInitialDelay(7, TimeUnit.DAYS)
                .addTag(ReviveUserWork.WEEK_DELAY_NOTIFICATION)
                .build()

        val secondNotificationWork =
            OneTimeWorkRequest
                .Builder(ReviveUserWork::class.java)
                .setInitialDelay(7, TimeUnit.DAYS)
                .addTag(ReviveUserWork.TWO_WEEKS_DELAY_NOTIFICATION)
                .build()

        val lastNotificationWork =
            OneTimeWorkRequest
                .Builder(ReviveUserWork::class.java)
                .setInitialDelay(14, TimeUnit.DAYS)
                .addTag(ReviveUserWork.MONTH_DELAY_NOTIFICATION)
                .build()

        workManager.cancelAllWork()

        workManager
            .beginWith(firstNotificationWork)
            .then(secondNotificationWork)
            .then(lastNotificationWork)
            .enqueue()

    }
}
