package updated.mysterium.vpn.ui.top.up.summary

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.asLiveData
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardSummaryBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.exceptions.TopupPreconditionFailedException
import updated.mysterium.vpn.model.payment.Order
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.model.top.up.TopUpPriceCardItem
import updated.mysterium.vpn.notification.PaymentStatusService
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
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
        paymentViewModel.billingDataSource.purchasePendingFlow.asLiveData().observe(this) {
            showPaymentProcessingBanner()
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
        binding.paymentProcessingLayout.closeBannerButton.setOnClickListener {
            binding.paymentProcessingLayout.root.visibility = View.GONE
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

        paymentStatusViewModel.getPayment(price).observe(this) {
            it.onSuccess { order ->
                topUpPriceCardItem = topUpPriceCardItem?.copy(id = order.id)
                inflateOrderData(order)
            }
            it.onFailure { error ->
                Log.e(TAG, error.message ?: error.toString())
                if (error is TopupPreconditionFailedException) {
                    showPaymentBalanceLimitError()
                }
            }
        }
    }

    private fun inflateOrderData(order: Order) {
        paymentViewModel.isBalanceLimitExceeded().observe(this) {
            it.onSuccess { isBalanceLimitExceeded ->
                if (isBalanceLimitExceeded) {
                    showPaymentBalanceLimitError()
                } else {
                    binding.confirmContainer.visibility = View.VISIBLE
                    binding.cancelContainer.visibility = View.INVISIBLE
                }
            }
        }
        binding.totalPriceValueTextView.text =
            getString(
                R.string.payment_myst_description,
                order.receiveMyst
            )
    }

    private fun launchPlayBillingPayment() {
        topUpPriceCardItem?.let {
            paymentViewModel.billingDataSource.launchBillingFlow(
                this@PaymentSummaryActivity,
                it.sku,
                it.id
            )
        }
    }

    private fun paymentConfirmed() {
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

    private fun showPaymentBalanceLimitError() {
        showBanner(binding.paymentBalanceLimitLayout.root)
        binding.confirmContainer.visibility = View.INVISIBLE
        binding.cancelContainer.visibility = View.VISIBLE
    }

    private fun showPaymentProcessingBanner() {
        showBanner(binding.paymentProcessingLayout.root)
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

    private fun navigateToHome() {
        viewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    private fun startService() {
        startService(Intent(this, PaymentStatusService::class.java))
    }
}
