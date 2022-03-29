package updated.mysterium.vpn.ui.top.up.coingate.amount

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityTopUpAmountBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.data.WalletEstimatesUtil
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.top.up.card.summary.CardSummaryActivity
import updated.mysterium.vpn.ui.top.up.coingate.crypto.TopUpCryptoActivity
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel
import java.util.*

class TopUpAmountActivity : BaseActivity() {

    companion object {
        const val PAYMENT_METHOD_EXTRA_KEY = "PAYMENT_METHOD_EXTRA_KEY"
    }

    private lateinit var binding: ActivityTopUpAmountBinding
    private val viewModel: TopUpViewModel by inject()
    private val walletViewModel: TopUpAmountViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val topUpAdapter = TopUpAmountAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
        handlePaymentMethod()
    }

    private fun configure() {
        binding.amountRecycler.adapter = topUpAdapter
        topUpAdapter.onItemSelected = {
            updateEquivalent(it.value.toDouble())
            updateWalletEstimates(it.value.toDouble())
        }
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            val gateway = intent.extras?.getSerializable(PAYMENT_METHOD_EXTRA_KEY) as Gateway
            if (gateway == Gateway.CARDINITY) {
                navigateToCardPaymentFlow()
            } else {
                navigateToCryptoPaymentFlow()
            }
        }
        binding.freeTrialButtonButton.setOnClickListener {
            viewModel.accountFlowShown()
            navigateToConnectionOrHome(isBackTransition = false)
        }
    }

    private fun handlePaymentMethod() {
        val gateway = intent.extras?.getSerializable(PAYMENT_METHOD_EXTRA_KEY) as Gateway
        viewModel.getUsdPrices(gateway).observe(this) {
            it.onSuccess { prices ->
                prices?.let {
                    topUpAdapter.replaceAll(prices)
                    prices.find { item ->
                        item.isSelected
                    }?.value?.let { price ->
                        val mystAmount = exchangeRateViewModel.mystEquivalent * price.toDouble()
                        updateEquivalent(mystAmount)
                        updateWalletEstimates(mystAmount)
                    }
                }
            }

            it.onFailure {
                wifiNetworkErrorPopUp()
            }
        }
    }

    private fun updateEquivalent(mystAmount: Double) {
        binding.mystEquivalentTextView.text = getString(
            R.string.top_up_usd_equivalent, mystAmount
        )
    }

    private fun updateWalletEstimates(mystAmount: Double) {
        walletViewModel.getWalletEquivalent(mystAmount).observe(this) { result ->
            result.onSuccess { estimates ->
                binding.videoTopUpItem.setData(WalletEstimatesUtil.convertVideoData(estimates))
                binding.videoTopUpItem.setType(
                    WalletEstimatesUtil.convertVideoType(estimates).toUpperCase(Locale.ROOT)
                )
                binding.pagesTopUpItem.setData(WalletEstimatesUtil.convertWebData(estimates))
                binding.pagesTopUpItem.setType(
                    WalletEstimatesUtil.convertWebType(estimates).toUpperCase(Locale.ROOT)
                )
                binding.trafficTopUpItem.setData(
                    WalletEstimatesUtil.convertDownloadData(estimates).toString()
                )
                binding.trafficTopUpItem.setType(WalletEstimatesUtil.convertDownloadType(estimates))
                binding.musicTopUpItem.setData(WalletEstimatesUtil.convertMusicTimeData(estimates))
                binding.musicTopUpItem.setType(
                    WalletEstimatesUtil.convertMusicTimeType(estimates).toUpperCase(Locale.ROOT)
                )
            }
        }
    }

    private fun navigateToCryptoPaymentFlow() {
        val intent = Intent(this, TopUpCryptoActivity::class.java).apply {
            val cryptoAmount = topUpAdapter.getSelectedValue()?.toInt()
            putExtra(TopUpCryptoActivity.CRYPTO_AMOUNT_EXTRA_KEY, cryptoAmount)
        }
        startActivity(intent)
    }

    private fun navigateToCardPaymentFlow() {
        val intent = Intent(this, CardSummaryActivity::class.java).apply {
            val usdPrice = topUpAdapter.getSelectedValue()?.toDouble() ?: 0.0
            val mystAmount = usdPrice * exchangeRateViewModel.mystEquivalent
            putExtra(CardSummaryActivity.USD_PRICE_EXTRA_KEY, usdPrice)
            putExtra(CardSummaryActivity.MYST_AMOUNT_EXTRA_KEY, mystAmount)
        }
        startActivity(intent)
    }
}
