package updated.mysterium.vpn.ui.top.up.card.summary

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardSummaryBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.exceptions.TopupPreconditionFailedException
import updated.mysterium.vpn.model.payment.CardOrder
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.model.top.up.TopUpPriceCardItem
import updated.mysterium.vpn.notification.PaymentStatusService
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.top.up.PaymentStatusViewModel
import updated.mysterium.vpn.ui.top.up.coingate.payment.TopUpPaymentViewModel
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel

class CardSummaryActivity : BaseActivity() {

    companion object {
        const val SKU_EXTRA_KEY = "SKU_EXTRA_KEY"
    }

    private lateinit var binding: ActivityCardSummaryBinding
    private val viewModel: CardSummaryViewModel by inject()
    private val paymentViewModel: TopUpPaymentViewModel by inject()
    private val paymentStatusViewModel: PaymentStatusViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
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

        paymentStatusViewModel.getPayment(price).observe(this) {
            it.onSuccess { order ->
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

    private fun inflateOrderData(cardOrder: CardOrder) {
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
        val mystEquivalent = exchangeRateViewModel.getMystEquivalent(cardOrder.orderTotalAmount)
        binding.totalPriceValueTextView.text =
            getString(
                R.string.card_payment_myst_description,
                mystEquivalent
            )
    }

    private fun launchPlayBillingPayment() {
        topUpPriceCardItem?.let {
            paymentViewModel.billingDataSource.launchBillingFlow(
                this@CardSummaryActivity,
                it.sku
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
