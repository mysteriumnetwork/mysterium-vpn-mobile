package updated.mysterium.vpn.ui.top.up.coingate.amount

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityTopUpAmountBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.data.WalletEstimatesUtil
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.top.up.card.currency.CardCurrencyActivity
import updated.mysterium.vpn.ui.top.up.coingate.crypto.TopUpCryptoActivity
import java.util.*

class TopUpAmountActivity : BaseActivity() {

    companion object {
        const val PAYMENT_METHOD_EXTRA_KEY = "PAYMENT_METHOD_EXTRA_KEY"
    }

    private lateinit var binding: ActivityTopUpAmountBinding
    private val viewModel: TopUpViewModel by inject()
    private val walletViewModel: TopUpAmountViewModel by inject()
    private val topUpAdapter = TopUpAmountUSDAdapter()
    private var gateway: Gateway? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
        handlePaymentMethod()
    }

    private fun configure() {
        gateway = Gateway.from(intent.extras?.getString(PAYMENT_METHOD_EXTRA_KEY))
        binding.priceRecycler.adapter = topUpAdapter
        topUpAdapter.onItemSelected = {
            updateWalletEstimates(it.amountUSD)
        }
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            if (gateway == Gateway.COINGATE) {
                navigateToCryptoPaymentFlow()
            } else {
                navigateToCardPaymentFlow()
            }
        }
    }

    private fun handlePaymentMethod() {
        gateway?.let { gateway ->
            viewModel.getAmountsUSD(gateway).observe(this) { result ->
                result.onSuccess { list ->
                    list?.let {
                        topUpAdapter.replaceAll(list)
                        val selectedItem = list.find { item -> item.isSelected }
                        selectedItem?.amountUSD?.let { amountUSD ->
                            updateWalletEstimates(amountUSD)
                        }
                    }
                }
                result.onFailure {
                    wifiNetworkErrorPopUp {
                        handlePaymentMethod()
                    }
                }
            }
        }
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
            val amountUSD = topUpAdapter.getSelectedValue()
            putExtra(TopUpCryptoActivity.CRYPTO_AMOUNT_USD_EXTRA_KEY, amountUSD)
        }
        startActivity(intent)
    }

    private fun navigateToCardPaymentFlow() {
        val intent = Intent(this, CardCurrencyActivity::class.java).apply {
            val amountUSD = topUpAdapter.getSelectedValue()
            putExtra(CardCurrencyActivity.AMOUNT_USD_EXTRA_KEY, amountUSD)
            putExtra(CardCurrencyActivity.GATEWAY_EXTRA_KEY, gateway?.gateway)
            putExtra(CardCurrencyActivity.GATEWAY_EXTRA_KEY, gateway?.gateway)
        }
        startActivity(intent)
    }
}
