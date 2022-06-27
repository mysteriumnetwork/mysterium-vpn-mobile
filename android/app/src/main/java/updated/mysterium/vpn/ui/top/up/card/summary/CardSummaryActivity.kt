package updated.mysterium.vpn.ui.top.up.card.summary

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardSummaryBinding
import network.mysterium.vpn.databinding.PopUpCardPaymentBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.exceptions.TopupPreconditionFailedException
import updated.mysterium.vpn.model.payment.CardOrder
import updated.mysterium.vpn.model.payment.PaymentStatus
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.notification.PaymentStatusService
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity.Companion.SHOW_PAYMENT_PROCESSING_BANNER_KEY
import updated.mysterium.vpn.ui.top.up.PaymentStatusViewModel
import updated.mysterium.vpn.ui.top.up.coingate.payment.TopUpPaymentViewModel

class CardSummaryActivity : BaseActivity() {

    companion object {
        const val AMOUNT_USD_EXTRA_KEY = "AMOUNT_USD_EXTRA_KEY"
        const val CURRENCY_EXTRA_KEY = "CURRENCY_EXTRA_KEY"
        const val COUNTRY_EXTRA_KEY = "COUNTRY_EXTRA_KEY"
        const val GATEWAY_EXTRA_KEY = "GATEWAY_EXTRA_KEY"
        private const val HTML_MIME_TYPE = "text/html"
        private const val ENCODING = "utf-8"
        private const val paymentCallbackUrl = "payment/stripe/redirect"
    }

    private lateinit var binding: ActivityCardSummaryBinding
    private val viewModel: CardSummaryViewModel by inject()
    private val paymentViewModel: TopUpPaymentViewModel by inject()
    private val paymentStatusViewModel: PaymentStatusViewModel by inject()
    private var paymentHtml: String? = null
    private var paymentProcessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeViewModel()
        bind()
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
            launchCardPayment()
        }
        binding.cancelButton.setOnClickListener {
            navigateToHome()
        }
        binding.closeButton.setOnClickListener {
            binding.closeButton.visibility = View.GONE
            binding.webView.visibility = View.GONE
            if (paymentProcessed) {
                showPaymentPopUp()
                paymentProcessed = false
            }
        }
        binding.paymentBalanceLimitLayout.closeBannerButton.setOnClickListener {
            binding.paymentBalanceLimitLayout.root.visibility = View.GONE
        }
    }

    private fun loadPayment() {
        val amountUSD = intent.extras?.getDouble(AMOUNT_USD_EXTRA_KEY) ?: return
        val currency = intent.extras?.getString(CURRENCY_EXTRA_KEY) ?: return
        val country = intent.extras?.getString(COUNTRY_EXTRA_KEY) ?: return
        val gateway = intent.extras?.getString(GATEWAY_EXTRA_KEY) ?: return

        startService()

        paymentStatusViewModel.getPayment(country, amountUSD, currency, gateway).observe(this) {
            it.onSuccess { order ->
                inflateOrderData(order)
                paymentHtml = order.pageHtml.htmlSecureData.toString()
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
        binding.mystTextView.text = getString(
            R.string.card_payment_myst_description, cardOrder.receiveMyst
        )
        binding.mystValueTextView.text = getString(
            R.string.top_up_amount_usd, cardOrder.payAmount
        )
        binding.vatValueTextView.text = cardOrder.taxes.toString()
        binding.totalValueTextView.text = getString(
            R.string.top_up_amount_usd, cardOrder.orderTotalAmount
        )

        val taxesPercent = cardOrder.taxes / cardOrder.orderTotalAmount * 100
        binding.vatTextView.text = getString(
            R.string.card_payment_vat_value, taxesPercent
        )
        binding.confirmContainer.visibility = View.VISIBLE
        binding.cancelContainer.visibility = View.INVISIBLE
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun launchCardPayment() {
        paymentHtml?.let { htmlData ->
            binding.closeButton.visibility = View.VISIBLE
            binding.webView.visibility = View.VISIBLE
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webViewClient = object : WebViewClient() {
                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    if (url?.contains(paymentCallbackUrl) == true) {
                        paymentProcessed = true
                    }
                }
            }
            binding.webView.loadDataWithBaseURL(null, htmlData, HTML_MIME_TYPE, ENCODING, null)
        }
    }

    private fun paymentConfirmed() {
        val amountUSD = intent.extras?.getDouble(AMOUNT_USD_EXTRA_KEY)
        val currency = intent.extras?.getString(CURRENCY_EXTRA_KEY)
        if (currency != null && amountUSD != null) {
            pushyNotifications.unsubscribe(PushyTopic.PAYMENT_FALSE)
            pushyNotifications.subscribe(PushyTopic.PAYMENT_TRUE)
            pushyNotifications.subscribe(currency)
            paymentViewModel.updateLastCurrency(currency)
        }
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

    private fun showPaymentPopUp() {
        val bindingPopUp = PopUpCardPaymentBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.okayButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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
            putExtra(SHOW_PAYMENT_PROCESSING_BANNER_KEY, true)
        }
        startActivity(intent)
    }

    private fun startService() {
        startService(Intent(this, PaymentStatusService::class.java))
    }
}
