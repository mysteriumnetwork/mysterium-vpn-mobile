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
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.*
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.AppConstants.Payments.PAYMENT_BALANCE_LIMIT
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.calculateRectOnScreen
import updated.mysterium.vpn.common.extensions.setVisibility
import updated.mysterium.vpn.exceptions.BaseNetworkException
import updated.mysterium.vpn.exceptions.TopupBalanceLimitException
import updated.mysterium.vpn.model.payment.Order
import updated.mysterium.vpn.notification.PaymentStatusService
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.top.up.crypto.currency.CryptoCurrencyActivity.Companion.CRYPTO_AMOUNT_USD_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.crypto.currency.CryptoCurrencyActivity.Companion.CRYPTO_IS_LIGHTNING_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.crypto.currency.CryptoCurrencyActivity.Companion.CRYPTO_NAME_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity.Companion.COUNTRY_EXTRA_KEY
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryActivity.Companion.STATE_EXTRA_KEY
import java.math.BigDecimal
import kotlin.math.abs

class CryptoPaymentActivity : BaseActivity() {

    companion object {
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
        binding.closeButton.setOnClickListener {
            navigateToHome()
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
        val initialBalance = balanceViewModeL.balanceLiveData.value ?: 0.0
        if (initialBalance >= PAYMENT_BALANCE_LIMIT) showPaymentBalanceLimitError(
            getString(
                R.string.payment_balance_limit_text,
                PAYMENT_BALANCE_LIMIT
            )
        )

        viewModel.channelAddress().observe(this) {
            stopLoaderAnimation()
            it.onSuccess { channelAddress ->
                setMystPolygonWaitingScreen(channelAddress)
                balanceViewModeL.balanceLiveData.observe(this) { newBalance ->
                    if (newBalance > initialBalance) {
                        setMystPolygonReceivedScreen(newBalance)
                    }
                }
            }
            it.onFailure {
                showTopUpServerFailed()
            }
        }
    }

    private fun createPaymentOrder() {
        val currency = intent.extras?.getString(CRYPTO_NAME_EXTRA_KEY) ?: ""
        val country = intent?.extras?.getString(COUNTRY_EXTRA_KEY) ?: ""
        val stateOfAmerica = intent?.extras?.getString(STATE_EXTRA_KEY) ?: ""
        val amountUSD = intent.extras?.getDouble(CRYPTO_AMOUNT_USD_EXTRA_KEY)
        val isLightning = intent.extras?.getBoolean(CRYPTO_IS_LIGHTNING_EXTRA_KEY)

        viewModel.createPaymentOrder(
            currency,
            country,
            stateOfAmerica,
            amountUSD ?: 0.0,
            isLightning ?: false
        ).observe(this) { result ->
            stopLoaderAnimation()
            result.onSuccess { order ->
                setCryptoOrderScreen(order, currency, amountUSD)
            }
            result.onFailure { error ->
                if (error is BaseNetworkException && error.exception is TopupBalanceLimitException) {
                    Log.e(TAG, error.getMessage(this))
                    showPaymentBalanceLimitError(
                        error.getMessage(this)
                    )
                } else {
                    Log.e(TAG, error.localizedMessage ?: error.toString())
                    showTopUpServerFailed()
                }
            }
        }
    }

    private fun setMystPolygonWaitingScreen(channelAddress: String) {
        binding.timer.setVisibility(false)
        setCurrencyEquivalentVisibility(false)
        binding.topUpDescription.text = getString(R.string.top_up_payment_myst_polygon_description)
        binding.polygonOnlyWarningCardView.setVisibility(true)
        showTopUpAddress(channelAddress)
        binding.usdEquivalentTextView.setVisibility(false)
        binding.receivedMystFrame.setVisibility(false)
        binding.paymentAnimation.setVisibility(true)
        binding.balanceRefreshingTextView.setVisibility(true)
        binding.closeButton.setVisibility(false)
    }

    private fun setMystPolygonReceivedScreen(newBalance: Double) {
        binding.receivedMystFrame.setVisibility(true)
        binding.receivedMystValueTextView.text =
            getString(R.string.top_up_payment_myst_description, newBalance)
        binding.paymentAnimation.setVisibility(false)
        binding.balanceRefreshingTextView.setVisibility(false)
        binding.closeButton.setVisibility(true)
    }

    private fun setCryptoOrderScreen(order: Order, currency: String, amountUSD: Double?) {
        binding.timer.setVisibility(true)
        binding.timer.startTimer()
        setCurrencyEquivalentVisibility(true, currency, order)
        binding.topUpDescription.text = getString(R.string.top_up_payment_crypto_description)
        binding.polygonOnlyWarningCardView.setVisibility(false)
        order.publicGatewayData.paymentURL?.let { link ->
            showTopUpAddress(link)
        }
        binding.usdEquivalentTextView.setVisibility(true)
        binding.usdEquivalentTextView.text = getString(R.string.top_up_usd_equivalent, amountUSD)
        showPaymentPopUp()
        binding.receivedMystFrame.setVisibility(false)
        binding.paymentAnimation.setVisibility(true)
        binding.balanceRefreshingTextView.setVisibility(false)
        binding.closeButton.setVisibility(false)
    }

    private fun stopLoaderAnimation() {
        binding.loader.visibility = View.GONE
        binding.loader.cancelAnimation()
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
        return abs(fullSize * 0.6).toInt() // size with spaces
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
            binding.currencyEquivalentFrame.visibility = View.GONE
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

    private fun showPaymentBalanceLimitError(message: String) {
        binding.paymentBalanceLimitLayout.balanceLimitTextView.text = message
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
