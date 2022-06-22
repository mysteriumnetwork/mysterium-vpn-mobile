package updated.mysterium.vpn.ui.top.up.coingate.crypto

import android.content.Intent
import android.os.Bundle
import android.view.View
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityTopUpCryptoBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.top.up.CryptoCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.custom.view.CryptoAnimationView
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.top.up.coingate.payment.TopUpPaymentActivity
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel

class TopUpCryptoActivity : BaseActivity() {

    companion object {
        const val CRYPTO_AMOUNT_USD_EXTRA_KEY = "CRYPTO_AMOUNT_USD_EXTRA_KEY"
    }

    private lateinit var binding: ActivityTopUpCryptoBinding
    private val viewModel: TopUpViewModel by inject()
    private val topUpAdapter = TopUpCryptoAdapter()
    private var amountUSD: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpCryptoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getCryptoValue()
        configure()
        bindsAction()
    }

    private fun configure() {
        binding.amountRecycler.adapter = topUpAdapter
        topUpAdapter.replaceAll(getCryptoList())
        topUpAdapter.onItemSelected = {
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
        binding.freeTrialButtonButton.setOnClickListener {
            viewModel.accountFlowShown()
            navigateToConnectionIfConnectedOrHome(isBackTransition = false)
        }
        binding.confirmButton.setOnClickListener {
            val cryptoName = topUpAdapter.getSelectedValue()
            val intent = Intent(this, TopUpPaymentActivity::class.java).apply {
                putExtra(TopUpPaymentActivity.CRYPTO_AMOUNT_USD_EXTRA_KEY, amountUSD)
                putExtra(TopUpPaymentActivity.CRYPTO_NAME_EXTRA_KEY, cryptoName)
                putExtra(
                    TopUpPaymentActivity.CRYPTO_IS_LIGHTNING_EXTRA_KEY,
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
        CryptoCardItem(CryptoAnimationView.MYST, false, R.raw.myst_animation, true),
        CryptoCardItem(CryptoAnimationView.BTC, true, R.raw.btc_animation),
        CryptoCardItem(CryptoAnimationView.ETH, false, R.raw.eth_animation),
        CryptoCardItem(CryptoAnimationView.LTC, true, R.raw.ltc_animation),
        CryptoCardItem(CryptoAnimationView.DAI, false, R.raw.dai_animation),
        CryptoCardItem(CryptoAnimationView.USDT, false, R.raw.t_animation),
        CryptoCardItem(CryptoAnimationView.DOGE, false, R.raw.doge_animation)
    )
}
