package updated.mysterium.vpn.ui.top.up.card.summary

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
import network.mysterium.vpn.databinding.PopUpInsufficientFundsBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.payment.CardOrder
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.top.up.coingate.amount.TopUpAmountActivity
import updated.mysterium.vpn.ui.top.up.coingate.payment.TopUpPaymentViewModel


class CardSummaryActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_EXTRA_KEY = "CRYPTO_AMOUNT_EXTRA_KEY"
        const val CRYPTO_CURRENCY_EXTRA_KEY = "CRYPTO_CURRENCY_EXTRA_KEY"
        const val COUNTRY_EXTRA_KEY = "COUNTRY_EXTRA_KEY"
        private const val TAG = "CardSummaryActivity"
        private const val HTML_MIME_TYPE = "text/html"
        private const val ENCODING = "utf-8"
    }

    private lateinit var binding: ActivityCardSummaryBinding
    private val viewModel: CardSummaryViewModel by inject()
    private val paymentViewModel: TopUpPaymentViewModel by inject()
    private var paymentHtml: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeViewModel()
        bind()
        getMystAmount()
        loadPayment()
    }

    private fun subscribeViewModel() {
        viewModel.paymentSuccessfully.observe(this, {
            val amount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY)
            val currency = intent.extras?.getString(CRYPTO_CURRENCY_EXTRA_KEY)
            if (currency != null && amount != null) {
                pushyNotifications.unsubscribe(PushyTopic.PAYMENT_FALSE)
                pushyNotifications.subscribe(PushyTopic.PAYMENT_TRUE)
                pushyNotifications.subscribe(currency)
                paymentViewModel.updateLastCurrency(currency)
            }
            paymentViewModel.clearPopUpTopUpHistory()
            registerAccount()
        })
    }

    private fun bind() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            launchCardinityPayment()
        }
        binding.closeButton.setOnClickListener {
            binding.closeButton.visibility = View.GONE
            binding.webView.visibility = View.GONE

            showPaymentPopUp()
        }
    }

    private fun showPaymentPopUp() {
        val bindingPopUp = PopUpCardPaymentBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.okayButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getMystAmount() {
        val mystAmount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY)
        binding.mystTextView.text = getString(
            R.string.card_payment_myst_description, mystAmount
        )
    }

    private fun loadPayment() {
        val amount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY) ?: return
        val currency = intent.extras?.getString(CRYPTO_CURRENCY_EXTRA_KEY) ?: return
        val country = intent.extras?.getString(COUNTRY_EXTRA_KEY) ?: return
        viewModel.getPayment(amount, country, currency).observe(this) {
            it.onSuccess { order ->
                inflateOrderData(order)
                paymentHtml = order.pageHtml.htmlSecureData.toString()
            }
            it.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
            }
        }
    }

    private fun inflateOrderData(cardOrder: CardOrder) {
        binding.mystValueTextView.text = cardOrder.payAmount.toString()
        binding.vatValueTextView.text = cardOrder.taxes.toString()
        binding.totalValueTextView.text = cardOrder.orderTotalAmount.toString()

        val taxesPercent = cardOrder.taxes / cardOrder.orderTotalAmount * 100
        binding.vatTextView.text = getString(
            R.string.card_payment_vat_value, taxesPercent
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun launchCardinityPayment() {
        paymentHtml?.let { htmlData ->
            binding.closeButton.visibility = View.VISIBLE
            binding.webView.visibility = View.VISIBLE
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webViewClient = object : WebViewClient() {

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    Log.i(TAG, url.toString())
                }
            }
            binding.webView.loadDataWithBaseURL(null, htmlData, HTML_MIME_TYPE, ENCODING, null)
        }
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

    private fun navigateToHome() {
        viewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
}
