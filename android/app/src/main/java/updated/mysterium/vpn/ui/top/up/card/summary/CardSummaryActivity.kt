package updated.mysterium.vpn.ui.top.up.card.summary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCardSummaryBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.payment.CardOrder
import updated.mysterium.vpn.ui.base.BaseActivity


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
    private var paymentHtml: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
        getMystAmount()
        loadPayment()
    }

    private fun bind() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            launchCardinityPayment()
        }
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
        binding.mystValueTextView.text = getString(
            R.string.card_payment_myst_amount, cardOrder.payAmount.toFloat()
        )
        binding.vatValueTextView.text = getString(
            R.string.card_payment_myst_amount, cardOrder.taxes.toFloat()
        )
        binding.totalValueTextView.text = getString(
            R.string.card_payment_myst_amount, cardOrder.orderTotalAmount.toFloat()
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun launchCardinityPayment() {
        val htmlBody = "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <title>Redirect</title>\n" +
                "        <style>\n" +
                "            body {\n" +
                "                background: linear-gradient(180deg, #562160 0%, #7B2061 48.96%, #64205D 100%);\n" +
                "                width: 100%;\n" +
                "                height: 100%;\n" +
                "                margin: 0;\n" +
                "                font-family: \"Segoe UI\", -apple-system, BlinkMacSystemFont, Ubuntu, sans-serif;\n" +
                "                line-height: 1.4;\n" +
                "                color: #ffffffc8;\n" +
                "            }\n" +
                "            div {\n" +
                "                position: relative;\n" +
                "                top: 50%;\n" +
                "                transform: translateY(-50%);\n" +
                "                text-align: center;\n" +
                "            }\n" +
                "            button {\n" +
                "                padding: 10px;\n" +
                "                font-size: 16px;\n" +
                "                line-height: 16px;\n" +
                "                border: 0;\n" +
                "                border-radius: 10px;\n" +
                "            }\n" +
                "        </style>\n" +
                "    </head>\n" +
                "    <body onload=\"document.forms['checkout'].submit()\">\n" +
                "    <div>\n" +
                "        <h2>Redirecting...</h2>\n" +
                "        <p>Taking you to the payment provider automatically.<br>If it does not work, click the button below.</p>\n" +
                "        <form name=\"checkout\" method=\"POST\" action=\"https://checkout.cardinity.com\">\n" +
                "            <button type=submit>Continue</button>\n" +
                "            <input type=\"hidden\" name=\"amount\" value=\"4.77\" />\n" +
                "            <input type=\"hidden\" name=\"country\" value=\"US\" />\n" +
                "            <input type=\"hidden\" name=\"currency\" value=\"USD\" />\n" +
                "            <input type=\"hidden\" name=\"order_id\" value=\"5a13ecca-e4d3-479f-9f79-30652fe6f5b1\" />\n" +
                "            <input type=\"hidden\" name=\"project_id\" value=\"pr_hbbivyxfnl4ehvhohs8dkavpfoueci\" />\n" +
                "            <input type=\"hidden\" name=\"return_url\" value=\"https://pilvytis.mysterium.network/api/v2/payment/cardinity/redirect\" />\n" +
                "            <input type=\"hidden\" name=\"signature\" value=\"82d9a9d4064eb6afd0496f9360ec3f9e22af95a5e870a526265a61b6811f2a02\" />\n" +
                "        </form>\n" +
                "    </div>\n" +
                "    </body>\n" +
                "    </html>"
        paymentHtml?.let { htmlData ->
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
}
