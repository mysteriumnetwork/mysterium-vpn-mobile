package updated.mysterium.vpn.ui.top.up.summary

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardSummaryBinding
import network.mysterium.vpn.databinding.PopUpCardPaymentBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.exceptions.TopupBalanceLimitException
import updated.mysterium.vpn.exceptions.TopupNoAmountException
import updated.mysterium.vpn.model.payment.Order
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.model.top.up.TopUpPriceCardItem
import updated.mysterium.vpn.notification.PaymentStatusService
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity.Companion.SHOW_PAYMENT_PROCESSING_BANNER_KEY
import updated.mysterium.vpn.ui.pop.up.PopUpNoAmount
import updated.mysterium.vpn.ui.top.up.PaymentStatusViewModel
import updated.mysterium.vpn.ui.top.up.TopUpPaymentViewModel

class PaymentSummaryActivity : BaseActivity() {

    companion object {
        const val SKU_EXTRA_KEY = "SKU_EXTRA_KEY"
    }

    private lateinit var binding: ActivityCardSummaryBinding
    private val viewModel: PaymentSummaryViewModel by inject()
    private val paymentViewModel: TopUpPaymentViewModel by inject()
    private val paymentStatusViewModel: PaymentStatusViewModel by inject()
    private var topUpPriceCardItem: TopUpPriceCardItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeViewModel()
        bind()
        getExtra()
        loadPayment()
    }

    private fun subscribeViewModel() {
        paymentStatusViewModel.paymentSuccessfully.observe(this) { paymentStatus ->
            if (paymentStatus == PaymentStatus.STATUS_PAID) {
                paymentConfirmed()
            }
        }
        viewModel.billingDataSource.purchaseUpdatedFlow.observe(this) {
            setButtonAvailability(false)
            showPaymentPopUp()
        }
    }

    private fun bind() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            launchPlayBillingPayment()
        }
        binding.cancelButton.setOnClickListener {
            navigateToHome()
        }
        binding.paymentBalanceLimitLayout.closeBannerButton.setOnClickListener {
            binding.paymentBalanceLimitLayout.root.visibility = View.GONE
        }
    }

    private fun getExtra() {
        intent.extras?.getParcelable<TopUpPriceCardItem>(SKU_EXTRA_KEY)?.let { topUpPriceCardItem ->
            this.topUpPriceCardItem = topUpPriceCardItem
        }
    }

    private fun loadPayment() {
        val price = topUpPriceCardItem?.price ?: 0.0
        startService()
        getPayment(price)
    }

    private fun getPayment(price: Double) {
        paymentStatusViewModel.getPayment(price).observe(this) {
            it.onSuccess { order ->
                topUpPriceCardItem = topUpPriceCardItem?.copy(id = order.id)
                if (topUpPriceCardItem?.id?.isEmpty() == true) {
                    showNoAmountPopUp { getPayment(price) }
                    return@onSuccess
                }
                inflateOrderData(order)
            }
            it.onFailure { error ->
                Log.e(TAG, error.message ?: error.toString())
                if (error is TopupBalanceLimitException) {
                    showBanner(binding.paymentBalanceLimitLayout.root)
                } else if (error is TopupNoAmountException) {
                    showNoAmountPopUp { getPayment(price) }
                }
                setButtonAvailability(false)
            }
        }
    }

    private fun inflateOrderData(order: Order) {
        binding.totalPriceValueTextView.text = getString(
            R.string.payment_myst_description,
            order.receiveMyst
        )
    }

    private fun launchPlayBillingPayment() {
        if (topUpPriceCardItem?.id?.isEmpty() == true) return

        topUpPriceCardItem?.let {
            paymentViewModel.billingDataSource.launchBillingFlow(
                this@PaymentSummaryActivity,
                it.sku,
                it.id
            )
        }
    }

    private fun paymentConfirmed() {
        setButtonAvailability(true)
        pushyNotifications.unsubscribe(PushyTopic.PAYMENT_FALSE)
        pushyNotifications.subscribe(PushyTopic.PAYMENT_TRUE)
        pushyNotifications.subscribe("USD")
        paymentViewModel.clearPopUpTopUpHistory()
        registerAccount()
    }

    private fun registerAccount() {
        paymentViewModel.registerAccount().observe(this) {
            it.onSuccess {
                navigateToHome()
            }
            it.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
                navigateToHome()
            }
        }
    }

    private fun showBanner(view: View) {
        view.visibility = View.VISIBLE
        val animationX =
            (binding.titleTextView.x + binding.titleTextView.height + resources.getDimension(R.dimen.margin_padding_size_medium))
        ObjectAnimator.ofFloat(
            view,
            "translationY",
            animationX
        ).apply {
            duration = 2000
            start()
        }
    }

    private fun showPaymentPopUp() {
        val bindingPopUp = PopUpCardPaymentBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.okayButton.setOnClickListener {
            dialog.dismiss()
            navigateToHome()
        }
        dialog.show()
    }

    private fun showNoAmountPopUp(onTryAgainClick: () -> Unit) {
        val popUpNoAmount = PopUpNoAmount(layoutInflater)
        val dialogNoAmount = createPopUp(popUpNoAmount.bindingPopUp.root, true)
        popUpNoAmount.apply {
            this.dialog = dialogNoAmount
            this.onTryAgainAction = onTryAgainClick
            setUp()
        }
        dialogNoAmount.show()
    }

    private fun setButtonAvailability(isAvailable: Boolean) {
        if (isAvailable) {
            binding.confirmContainer.visibility = View.VISIBLE
            binding.cancelContainer.visibility = View.INVISIBLE
        } else {
            binding.confirmContainer.visibility = View.INVISIBLE
            binding.cancelContainer.visibility = View.VISIBLE
        }
    }

    private fun navigateToHome() {
        viewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(SHOW_PAYMENT_PROCESSING_BANNER_KEY, true)
        }
        startActivity(intent)
    }

    private fun startService() {
        startService(Intent(this, PaymentStatusService::class.java))
    }
}
