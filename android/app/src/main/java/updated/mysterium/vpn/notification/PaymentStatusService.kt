package updated.mysterium.vpn.notification

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.balance.BalanceViewModel

class PaymentStatusService : Service(), KoinComponent {

    private companion object {
        val TAG: String = PaymentStatusService::class.java.simpleName
    }

    private val useCaseProvider: UseCaseProvider by inject()
    private val notificationManager: AppNotificationManager by inject()
    private val paymentUseCase = useCaseProvider.payment()
    private val balanceViewModel: BalanceViewModel by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindPaymentCallback()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent) = Binder()

    private fun bindPaymentCallback() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, exception.localizedMessage ?: exception.toString())
        }
        CoroutineScope(Dispatchers.Main + handler).launch {
            paymentUseCase.paymentOrderCallback {
                handleCallbackResult(PaymentStatus.from(it.status), it.payAmount, it.payCurrency)
            }
        }
    }

    private fun handleCallbackResult(
        paymentStatus: PaymentStatus?,
        amount: String,
        currency: String
    ) {
        if (paymentStatus == PaymentStatus.STATUS_PAID) {
            notificationManager.showSuccessPaymentNotification(amount, currency)
            balanceViewModel.forceBalanceUpdate()
        } else {
            notificationManager.showFailedPaymentNotification()
        }
        stopSelf()
    }
}
