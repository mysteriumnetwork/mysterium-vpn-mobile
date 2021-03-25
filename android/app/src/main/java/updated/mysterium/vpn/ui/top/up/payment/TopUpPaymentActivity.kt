package updated.mysterium.vpn.ui.top.up.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import network.mysterium.payment.Order
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityTopUpPaymentBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import java.math.BigDecimal

class TopUpPaymentActivity : AppCompatActivity() {

    companion object {
        const val CRYPTO_AMOUNT_EXTRA_KEY = "CRYPTO_AMOUNT_EXTRA_KEY"
        const val CRYPTO_NAME_EXTRA_KEY = "CRYPTO_NAME_EXTRA_KEY"
        const val CRYPTO_IS_LIGHTING_EXTRA_KEY = "CRYPTO_IS_LIGHTING_EXTRA_KEY"
        private const val TAG = "TopUpPaymentActivity"
        private const val COPY_LABEL = "User identity address"
    }

    private lateinit var binding: ActivityTopUpPaymentBinding
    private val topUpViewModel: TopUpViewModel by inject()
    private val vieModel: TopUpPaymentViewModel by inject()
    private var link: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getExtra()
        bindsAction()
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.copyButton.setOnClickListener {
            copyToClipboard()
        }
    }

    private fun getExtra() {
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
        vieModel.createPaymentOrder(
            currency,
            amount?.toDouble() ?: 0.0,
            isLighting ?: false
        ).observe(this, { result ->
            binding.loader.visibility = View.GONE
            binding.loader.cancelAnimation()
            binding.qrShadow.visibility = View.VISIBLE
            binding.qrCodeFrame.visibility = View.VISIBLE
            binding.currencyEquivalentFrame.visibility = View.VISIBLE
            result.onSuccess {
                link = it.paymentURL
                link?.let { qrLink ->
                    showQrCode(qrLink)
                }
                showEquivalent(currency, it)
            }
            result.onFailure {
                Log.e(TAG, it.localizedMessage ?: it.toString())
            }
        })
    }

    private fun showQrCode(link: String) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(link, BarcodeFormat.QR_CODE, 500, 500)
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

    private fun showToast() {
        Toast.makeText(
            this,
            getString(R.string.profile_copy_to_clipboard),
            Toast.LENGTH_LONG
        ).show()
    }
}
