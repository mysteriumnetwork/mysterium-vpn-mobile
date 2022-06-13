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
            updateWalletEstimates(it.value.toDouble())
        }
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            val gateway = Gateway.from(intent.extras?.getString(PAYMENT_METHOD_EXTRA_KEY))
            if (gateway == Gateway.CARDINITY) {
                navigateToCardPaymentFlow()
            } else {
                navigateToCryptoPaymentFlow()
            }
        }
        binding.freeTrialButtonButton.setOnClickListener {
            viewModel.accountFlowShown()
            navigateToConnectionIfConnectedOrHome(isBackTransition = false)
        }
    }

    private fun handlePaymentMethod() {
        Gateway.from(intent.extras?.getString(PAYMENT_METHOD_EXTRA_KEY))?.let { gateway ->
            viewModel.getAmounts(gateway).observe(this) {
                it.onSuccess { amounts ->
                    amounts?.let {
                        topUpAdapter.replaceAll(amounts)
                        amounts.find { item ->
                            item.isSelected
                        }?.value?.let { amount ->
                            updateWalletEstimates(amount.toDouble())
                        }
                    }
                }

                it.onFailure {
                    wifiNetworkErrorPopUp {
                        handlePaymentMethod()
                    }
                }
            }
        }
    }

    private fun updateWalletEstimates(balance: Double) {
        walletViewModel.getWalletEquivalent(balance).observe(this) { result ->
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
        val intent = Intent(this, CardCurrencyActivity::class.java).apply {
            val cryptoAmount = topUpAdapter.getSelectedValue()?.toInt()
            putExtra(CardCurrencyActivity.CRYPTO_AMOUNT_EXTRA_KEY, cryptoAmount)
        }
        startActivity(intent)
    }
}
