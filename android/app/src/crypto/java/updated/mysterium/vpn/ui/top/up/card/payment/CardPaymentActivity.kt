package updated.mysterium.vpn.ui.top.up.card.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import network.mysterium.vpn.databinding.ActivityPaymentWebviewBinding
import network.mysterium.vpn.databinding.PopUpCardPaymentBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.AppConstants
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity

class CardPaymentActivity : BaseActivity() {

    companion object {
        const val PAYMENT_URL_KEY = "PAYMENT_URL_KEY"
    }

    private lateinit var binding: ActivityPaymentWebviewBinding
    private val viewModel: CardPaymentViewModel by inject()
    private var paymentUrl: String? = null
    private var paymentProcessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindActions()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configure() {
        paymentUrl = intent.extras?.getString(PAYMENT_URL_KEY)
        paymentUrl?.let { url ->
            binding.closeButton.visibility = View.VISIBLE
            binding.webView.visibility = View.VISIBLE
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webViewClient = object : WebViewClient() {

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    url?.let {
                        if (it.contains(AppConstants.Payments.STRIPE_CALLBACK_URL) ||
                            it.contains(AppConstants.Payments.PAYPAL_CALLBACK_URL)
                        ) {
                            paymentProcessed = true
                        }
                    }
                }
            }
            binding.webView.loadUrl(url)
        }
    }

    private fun bindActions() {
        binding.closeButton.setOnClickListener {
            binding.closeButton.visibility = View.GONE
            binding.webView.visibility = View.GONE
            if (paymentProcessed) {
                showPaymentPopUp()
                paymentProcessed = false
            } else {
                finish()
            }
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

    private fun navigateToHome() {
        viewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(HomeSelectionActivity.SHOW_PAYMENT_PROCESSING_BANNER_KEY, true)
        }
        startActivity(intent)
    }

}
