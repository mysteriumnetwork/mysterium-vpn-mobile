package updated.mysterium.vpn.ui.top.up.crypto.currency

import android.content.Intent
import android.os.Bundle
import android.view.View
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCryptoCurrencyBinding
import updated.mysterium.vpn.model.top.up.CryptoCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.custom.view.CryptoAnimationView
import updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentActivity

class CryptoCurrencyActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_USD_EXTRA_KEY = "CRYPTO_AMOUNT_USD_EXTRA_KEY"
    }

    private lateinit var binding: ActivityCryptoCurrencyBinding
    private val adapter = CryptoCurrencyAdapter()
    private var amountUSD: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCryptoCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getCryptoValue()
        configure()
        bindsAction()
    }

    private fun configure() {
        binding.amountRecycler.adapter = adapter
        adapter.replaceAll(getCryptoList())
        adapter.onItemSelected = {
            binding.cryptoAnimation.changeAnimation(it.value)
            if (it.isLightningAvailable) {
                binding.switchFrame.visibility = View.VISIBLE
            } else {
                binding.switchFrame.visibility = View.INVISIBLE
            }
        }
        binding.cryptoAnimation.changeAnimation(getCryptoList().first().value)
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            val cryptoName = adapter.getSelectedValue()
            val intent = Intent(this, CryptoPaymentActivity::class.java).apply {
                putExtra(CryptoPaymentActivity.CRYPTO_AMOUNT_USD_EXTRA_KEY, amountUSD)
                putExtra(CryptoPaymentActivity.CRYPTO_NAME_EXTRA_KEY, cryptoName)
                putExtra(
                    CryptoPaymentActivity.CRYPTO_IS_LIGHTNING_EXTRA_KEY,
                    binding.lightningSwitch.isChecked
                )
            }
            startActivity(intent)
        }
    }

    private fun getCryptoValue() {
        amountUSD = intent.extras?.getDouble(CRYPTO_AMOUNT_USD_EXTRA_KEY)
        binding.usdEquivalentTextView.text = getString(
            R.string.top_up_usd_equivalent, amountUSD
        )
    }

    private fun getCryptoList() = listOf(
        CryptoCardItem(CryptoAnimationView.BTC, true, R.raw.btc_animation, true),
        CryptoCardItem(CryptoAnimationView.ETH, false, R.raw.eth_animation),
        CryptoCardItem(CryptoAnimationView.LTC, true, R.raw.ltc_animation),
        CryptoCardItem(CryptoAnimationView.DAI, false, R.raw.dai_animation),
        CryptoCardItem(CryptoAnimationView.USDT, false, R.raw.t_animation),
        CryptoCardItem(CryptoAnimationView.DOGE, false, R.raw.doge_animation)
    )
}
