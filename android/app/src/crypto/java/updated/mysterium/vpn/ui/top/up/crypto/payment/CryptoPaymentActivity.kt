package updated.mysterium.vpn.ui.top.up.crypto.payment

import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.lifecycle.distinctUntilChanged
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.*
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.calculateRectOnScreen
import updated.mysterium.vpn.common.extensions.observeOnce
import updated.mysterium.vpn.exceptions.TopupBalanceLimitException
import updated.mysterium.vpn.model.payment.Order
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.notification.PaymentStatusService
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import java.math.BigDecimal
import kotlin.math.abs

class CryptoPaymentActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_USD_EXTRA_KEY = "CRYPTO_AMOUNT_USD_EXTRA_KEY"
        const val CRYPTO_NAME_EXTRA_KEY = "CRYPTO_NAME_EXTRA_KEY"
        const val CRYPTO_IS_LIGHTNING_EXTRA_KEY = "CRYPTO_IS_LIGHTNING_EXTRA_KEY"
        const val MYST_POLYGON_EXTRA_KEY = "MYST_POLYGON_EXTRA_KEY"
        private const val COPY_LABEL = "User identity address"
        private const val ANIMATION_MARGIN = 80
    }

    private lateinit var binding: ActivityCryptoPaymentBinding
    private val viewModel: CryptoPaymentViewModel by inject()
    private val balanceViewModeL: BalanceViewModel by inject()
    private var link: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCryptoPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getExtra()
        subscribeViewModel()
        bindsAction()
    }

    private fun subscribeViewModel() {
        viewModel.paymentSuccessfully.observe(this) {
            val currency = intent.extras?.getString(CRYPTO_NAME_EXTRA_KEY)
            val amountUSD = intent.extras?.getDouble(CRYPTO_AMOUNT_USD_EXTRA_KEY)?.toFloat()
            if (currency != null && amountUSD != null) {
                pushyNotifications.unsubscribe(PushyTopic.PAYMENT_FALSE)
                pushyNotifications.subscribe(PushyTopic.PAYMENT_TRUE)
                pushyNotifications.subscribe(currency)
                viewModel.updateLastCurrency(currency)
            }
            viewModel.clearPopUpTopUpHistory()
            registerAccount()
        }
        viewModel.paymentExpired.observe(this) {
            showTopUpExpired()
        }
        viewModel.paymentFailed.observe(this) {
            showTopUpServerFailed()
        }
        viewModel.paymentCanceled.observe(this) {
            showTopUpCanceled()
        }
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
        binding.paymentBalanceLimitLayout.closeBannerButton.setOnClickListener {
            binding.paymentBalanceLimitLayout.root.visibility = View.GONE
        }
    }

    private fun getExtra() {
        setPaymentAnimationParams()
        startService()
        if (intent.extras?.getBoolean(MYST_POLYGON_EXTRA_KEY) == true) {
            waitForPayment()
        } else {
            createPaymentOrder()
        }
    }

    private fun setPaymentAnimationParams() {
        val param = binding.paymentAnimation.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0, ANIMATION_MARGIN, 0, ANIMATION_MARGIN)
        binding.paymentAnimation.layoutParams = param
    }

    private fun waitForPayment() {
        viewModel.channelAddress().observe(this) {
            setLoaderVisibility(false)
            it.onSuccess { channelAddress ->
                showTopUpAddress(channelAddress)
                setCurrencyEquivalentVisibility(false)
                setUsdEquivalentVisibility(false)
                setTimerVisibility(false)
                val initialBalance = balanceViewModeL.balanceLiveData.value
                balanceViewModeL.balanceLiveData.distinctUntilChanged()
                    .observeOnce(this) { newBalance ->
                        if (initialBalance != newBalance) showTopUpSuccessfully()
                    }
            }
            it.onFailure {
                showTopUpServerFailed()
            }
        }
    }

    private fun createPaymentOrder() {
        val currency = intent.extras?.getString(CRYPTO_NAME_EXTRA_KEY) ?: ""
        val amountUSD = intent.extras?.getDouble(CRYPTO_AMOUNT_USD_EXTRA_KEY)
        val isLightning = intent.extras?.getBoolean(CRYPTO_IS_LIGHTNING_EXTRA_KEY)

        viewModel.createPaymentOrder(
            currency,
            amountUSD ?: 0.0,
            isLightning ?: false
        ).observe(this) { result ->
            setLoaderVisibility(false)
            result.onSuccess { order ->
                order.publicGatewayData.paymentURL?.let { link ->
                    showTopUpAddress(link)
                }
                setCurrencyEquivalentVisibility(true, currency, order)
                setUsdEquivalentVisibility(true, amountUSD)
                setTimerVisibility(true)
                showPaymentPopUp()
            }
            result.onFailure { exception ->
                Log.e(TAG, exception.localizedMessage ?: exception.toString())
                if (exception is TopupBalanceLimitException) {
                    showPaymentBalanceLimitError()
                } else {
                    showTopUpServerFailed()
                }
            }
        }
    }

    private fun setLoaderVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.loader.visibility = View.VISIBLE
        } else {
            binding.loader.visibility = View.GONE
            binding.loader.cancelAnimation()
        }
    }

    private fun setTimerVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.timer.visibility = View.VISIBLE
            binding.timer.startTimer()
        } else {
            binding.timer.visibility = View.INVISIBLE
        }
    }

    private fun showTopUpAddress(link: String) {
        this.link = link
        binding.linkValueTextView.text = link
        showQrCode(link)
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
        binding.qrShadow.visibility = View.VISIBLE
        binding.qrCodeFrame.visibility = View.VISIBLE
    }

    private fun calculateQrSize(): Int {
        val topCoordinate = binding.currencyEquivalentFrame.calculateRectOnScreen().bottom
        val bottomCoordinate = binding.topUpDescription.calculateRectOnScreen().top
        val fullSize = topCoordinate - bottomCoordinate
        return abs(fullSize * 0.8).toInt() // size with spaces
    }

    private fun setCurrencyEquivalentVisibility(
        isVisible: Boolean,
        currency: String? = null,
        order: Order? = null
    ) {
        if (isVisible) {
            binding.currencyEquivalentFrame.visibility = View.VISIBLE
            val amount = order?.payAmount // round currency amount
                ?.toBigDecimal()
                ?.setScale(6, BigDecimal.ROUND_HALF_EVEN)
                ?.toPlainString()
            binding.currencyEquivalentTextView.text = getString(
                R.string.top_up_currency_equivalent, amount, currency
            )
        } else {
            binding.currencyEquivalentFrame.visibility = View.INVISIBLE
        }
    }

    private fun setUsdEquivalentVisibility(isVisible: Boolean, amountUSD: Double? = null) {
        if (isVisible && amountUSD != null) {
            binding.usdEquivalentTextView.visibility = View.VISIBLE
            binding.usdEquivalentTextView.text = getString(
                R.string.top_up_usd_equivalent, amountUSD
            )
        } else {
            binding.usdEquivalentTextView.visibility = View.INVISIBLE
        }
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
            dialog.dismiss()
            navigateToHome()
        }
        dialog.show()
    }

    private fun showTopUpCanceled() {
        val bindingPopUp = PopUpPaymentCanceledBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showTopUpServerFailed() {
        val bindingPopUp = PopUpPaymentNotWorkBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.closeButton.setOnClickListener {
            dialog.dismiss()
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
            dialog.dismiss()
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

    private fun showPaymentBalanceLimitError() {
        binding.paymentBalanceLimitLayout.root.doOnLayout { view ->
            val animationY = binding.backButton.y + binding.backButton.height + view.height
            view.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(view, "translationY", animationY)
                .apply {
                    duration = 2000
                    start()
                }
        }
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
        viewModel.accountFlowShown()
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
