package updated.mysterium.vpn.ui.top.up.coingate.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.*
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.calculateRectOnScreen
import updated.mysterium.vpn.model.payment.Order
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.notification.PaymentStatusService
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel
import java.math.BigDecimal
import kotlin.math.abs

class TopUpPaymentActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_EXTRA_KEY = "CRYPTO_AMOUNT_EXTRA_KEY"
        const val CRYPTO_NAME_EXTRA_KEY = "CRYPTO_NAME_EXTRA_KEY"
        const val CRYPTO_IS_LIGHTING_EXTRA_KEY = "CRYPTO_IS_LIGHTING_EXTRA_KEY"
        private const val COPY_LABEL = "User identity address"
        private const val ANIMATION_MARGIN = 80
        private const val TAG = "TopUpPaymentActivity"
    }

    private lateinit var binding: ActivityTopUpPaymentBinding
    private val topUpViewModel: TopUpViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val viewModel: TopUpPaymentViewModel by inject()
    private var link: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getExtra()
        subscribeViewModel()
        bindsAction()
    }

    private fun subscribeViewModel() {
        viewModel.paymentSuccessfully.observe(this, {
            val currency = intent.extras?.getString(CRYPTO_NAME_EXTRA_KEY)
            val amount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY)?.toFloat()
            if (currency != null && amount != null) {
                pushyNotifications.unsubscribe(PushyTopic.PAYMENT_FALSE)
                pushyNotifications.subscribe(PushyTopic.PAYMENT_TRUE)
                pushyNotifications.subscribe(currency)
                viewModel.updateLastCurrency(currency)
            }
            viewModel.clearPopUpTopUpHistory()
            registerAccount()
        })
        viewModel.paymentExpired.observe(this, {
            showTopUpExpired()
        })
        viewModel.paymentFailed.observe(this, {
            showTopUpServerFailed()
        })
        viewModel.paymentCanceled.observe(this, {
            showTopUpCanceled()
        })
    }

    private fun registerAccount() {
        viewModel.registerAccount().observe(this) {
            it.onSuccess {
                showTopUpSuccessfully()
            }

            it.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
                showRegistrationErrorPopUp()
            }
        }
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.copyButton.setOnClickListener {
            copyToClipboard()
        }
        binding.freeTrialButtonButton.setOnClickListener {
            topUpViewModel.accountFlowShown()
            navigateToConnectionOrHome(isBackTransition = false)
        }
    }

    private fun getExtra() {
        val param = binding.paymentAnimation.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0, ANIMATION_MARGIN, 0, ANIMATION_MARGIN)
        binding.paymentAnimation.layoutParams = param

        val currency = intent.extras?.getString(CRYPTO_NAME_EXTRA_KEY) ?: ""
        val amount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY)
        val isLighting = intent.extras?.getBoolean(CRYPTO_IS_LIGHTING_EXTRA_KEY)
        amount?.let {
            binding.usdEquivalentTextView.text = getString(
                R.string.top_up_usd_equivalent, exchangeRateViewModel.usdEquivalent * it
            )
        }

        startService()

        viewModel.createPaymentOrder(
            currency,
            amount?.toDouble() ?: 0.0,
            isLighting ?: false
        ).observe(this, { result ->
            binding.loader.visibility = View.GONE
            binding.loader.cancelAnimation()
            result.onSuccess {
                paymentLoaded(currency, it)
                showPaymentPopUp()
            }
            result.onFailure {
                showTopUpServerFailed()
            }
        })
    }

    private fun paymentLoaded(currency: String, order: Order) {
        link = order.paymentURL
        order.paymentURL?.let { qrLink ->
            showQrCode(qrLink)
        }
        showEquivalent(currency, order)
        binding.timer.visibility = View.VISIBLE
        binding.timer.startTimer()
        binding.qrShadow.visibility = View.VISIBLE
        binding.qrCodeFrame.visibility = View.VISIBLE
        binding.currencyEquivalentFrame.visibility = View.VISIBLE
    }

    private fun showQrCode(link: String) {
        val barcodeEncoder = BarcodeEncoder()
        val qrSize = calculateQrSize()
        val bitmap = barcodeEncoder.encodeBitmap(
            link,
            BarcodeFormat.QR_CODE,
            qrSize,
            qrSize
        )
        binding.qrCode.setImageBitmap(bitmap)
    }

    private fun calculateQrSize(): Int {
        val topCoordinate = binding.currencyEquivalentFrame.calculateRectOnScreen().bottom
        val bottomCoordinate = binding.topUpDescription.calculateRectOnScreen().top
        val fullSize = topCoordinate - bottomCoordinate
        return abs(fullSize * 0.8).toInt() // size with spaces
    }

    private fun showEquivalent(currency: String, order: Order) {
        binding.linkValueTextView.text = link
        val amount = order.payAmount // round currency amount
            ?.toBigDecimal()
            ?.setScale(6, BigDecimal.ROUND_HALF_EVEN)
            ?.toPlainString()
        binding.currencyEquivalentTextView.text = getString(
            R.string.top_up_currency_equivalent, amount, currency
        )
    }

    private fun copyToClipboard() {
        ContextCompat.getSystemService(this, ClipboardManager::class.java)?.let { clipManager ->
            val clipData = ClipData.newPlainText(COPY_LABEL, link)
            clipManager.setPrimaryClip(clipData)
            showToast()
        }
    }

    private fun showTopUpSuccessfully() {
        val bindingPopUp = PopUpPaymentSuccessfullyBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.closeButton.setOnClickListener {
            dialog.hide()
            navigateToHome()
        }
        dialog.show()
    }

    private fun showTopUpCanceled() {
        val bindingPopUp = PopUpPaymentCanceledBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.closeButton.setOnClickListener {
            dialog.hide()
            navigateToHome()
        }
        dialog.show()
    }

    private fun showTopUpServerFailed() {
        val bindingPopUp = PopUpPaymentNotWorkBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.closeButton.setOnClickListener {
            navigateToHome()
        }
        dialog.show()
    }

    private fun showTopUpExpired() {
        val bindingPopUp = PopUpPaymentExpiredBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.tryAgainButton.setOnClickListener {
            dialog.dismiss()
            getExtra()
        }
        bindingPopUp.topUpLaterButton.setOnClickListener {
            navigateToHome()
        }
        dialog.show()
    }

    private fun showPaymentPopUp() {
        val bindingPopUp = PopUpCryptoPaymentBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.okayButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRegistrationErrorPopUp() {
        val bindingPopUp = PopUpRetryRegistrationBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.tryAgainButton.setOnClickListener {
            dialog.dismiss()
            viewModel.registerAccount()
        }
        bindingPopUp.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigateToHome() {
        topUpViewModel.accountFlowShown()
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }

    private fun showToast() {
        Toast.makeText(
            this,
            getString(R.string.profile_copy_to_clipboard),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun startService() {
        startService(Intent(this, PaymentStatusService::class.java))
    }
}
