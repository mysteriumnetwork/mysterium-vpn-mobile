package updated.mysterium.vpn.ui.top.up.payment

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
import network.mysterium.payment.Order
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityTopUpPaymentBinding
import network.mysterium.vpn.databinding.PopUpPaymentExpiredBinding
import network.mysterium.vpn.databinding.PopUpPaymentNotWorkBinding
import network.mysterium.vpn.databinding.PopUpPaymentSuccessfullyBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.wallet.WalletActivity
import java.math.BigDecimal

class TopUpPaymentActivity : BaseActivity() {

    companion object {
        const val TRIAL_MODE_EXTRA_KEY = "TRIAL_MODE_EXTRA_KEY"
        const val CRYPTO_AMOUNT_EXTRA_KEY = "CRYPTO_AMOUNT_EXTRA_KEY"
        const val CRYPTO_NAME_EXTRA_KEY = "CRYPTO_NAME_EXTRA_KEY"
        const val CRYPTO_IS_LIGHTING_EXTRA_KEY = "CRYPTO_IS_LIGHTING_EXTRA_KEY"
        private const val TAG = "TopUpPaymentActivity"
        private const val COPY_LABEL = "User identity address"
        private const val QR_CODE_SIZE = 500
        private const val ANIMATION_MARGIN = 80
    }

    private lateinit var binding: ActivityTopUpPaymentBinding
    private val topUpViewModel: TopUpViewModel by inject()
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
            viewModel.clearPopUpTopUpHistory()
            showTopUpSuccessfully()
        })
        viewModel.paymentExpired.observe(this, {
            showTopUpExpired()
        })
        viewModel.paymentFailed.observe(this, {
            showTopUpServerFailed()
        })
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
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun getExtra() {
        checkTrialState()
        val currency = intent.extras?.getString(CRYPTO_NAME_EXTRA_KEY) ?: ""
        val amount = intent.extras?.getInt(CRYPTO_AMOUNT_EXTRA_KEY)
        val isLighting = intent.extras?.getBoolean(CRYPTO_IS_LIGHTING_EXTRA_KEY)
        amount?.let {
            topUpViewModel.getUsdEquivalent(it).observe(this, { result ->
                result.onSuccess { equivalent ->
                    binding.usdEquivalentTextView.text = getString(
                        R.string.top_up_usd_equivalent, equivalent
                    )
                }
                result.onFailure { throwable ->
                    Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                    // TODO("Add UI error, failed to load equivalent")
                }
            })
        }
        viewModel.createPaymentOrder(
            currency,
            amount?.toDouble() ?: 0.0,
            isLighting ?: false
        ).observe(this, { result ->
            binding.loader.visibility = View.GONE
            binding.loader.cancelAnimation()
            result.onSuccess {
                paymentLoaded(currency, it)
            }
            result.onFailure {
                showTopUpServerFailed()
            }
        })
    }

    private fun paymentLoaded(currency: String, order: Order) {
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

    private fun checkTrialState() {
        if (intent.extras?.getBoolean(TRIAL_MODE_EXTRA_KEY) == true) {
            binding.freeTrialButtonButton.visibility = View.VISIBLE
        } else {
            val param = binding.paymentAnimation.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, ANIMATION_MARGIN, 0, ANIMATION_MARGIN)
            binding.paymentAnimation.layoutParams = param
            binding.freeTrialButtonButton.visibility = View.GONE
        }
    }

    private fun showQrCode(link: String) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(
            link,
            BarcodeFormat.QR_CODE,
            QR_CODE_SIZE,
            QR_CODE_SIZE
        )
        binding.qrCode.setImageBitmap(bitmap)
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
            val intent = Intent(this, WalletActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
        dialog.show()
    }

    private fun showTopUpServerFailed() {
        val bindingPopUp = PopUpPaymentNotWorkBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.closeButton.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
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
            val intent = Intent(this, WalletActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
        dialog.show()
    }

    private fun showToast() {
        Toast.makeText(
            this,
            getString(R.string.profile_copy_to_clipboard),
            Toast.LENGTH_LONG
        ).show()
    }
}
