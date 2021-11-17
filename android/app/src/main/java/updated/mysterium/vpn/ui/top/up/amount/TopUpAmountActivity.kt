package updated.mysterium.vpn.ui.top.up.amount

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityTopUpAmountBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.data.WalletEstimatesUtil
import updated.mysterium.vpn.model.top.up.TopUpCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.payment.method.PaymentMethodActivity
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel
import java.util.*

class TopUpAmountActivity : BaseActivity() {

    companion object {
        const val REGISTRATION_MODE_EXTRA_KEY = "REGISTRATION_MODE_EXTRA_KEY"
        private val AMOUNT_VALUES = listOf(
            TopUpCardItem("20", true),
            TopUpCardItem("40"),
            TopUpCardItem("60"),
            TopUpCardItem("80"),
            TopUpCardItem("100")
        )
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
    }

    private fun configure() {
        binding.amountRecycler.adapter = topUpAdapter
        topUpAdapter.replaceAll(AMOUNT_VALUES)
        topUpAdapter.onItemSelected = {
            updateEquivalent(it.value.toInt())
            updateWalletEstimates(it.value.toDouble())
        }
        updateEquivalent(AMOUNT_VALUES.first().value.toInt())
        updateWalletEstimates(AMOUNT_VALUES.first().value.toDouble())
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            val cryptoAmount = topUpAdapter.getSelectedValue()?.toInt()
            navigateToPaymentMethod(cryptoAmount)
        }
        binding.freeTrialButtonButton.setOnClickListener {
            viewModel.accountFlowShown()
            navigateToConnectionOrHome(isBackTransition = false)
        }
    }

    private fun updateEquivalent(value: Int) {
        binding.usdEquivalentTextView.text = getString(
            R.string.top_up_usd_equivalent, exchangeRateViewModel.usdEquivalent * value
        )
    }

    private fun updateWalletEstimates(balance: Double) {
        walletViewModel.getWalletEquivalent(balance).observe(this, { result ->
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
        })
    }

    private fun navigateToPaymentMethod(cryptoAmount: Int?) {
        val intent = Intent(this, PaymentMethodActivity::class.java).apply {
            putExtra(PaymentMethodActivity.CRYPTO_AMOUNT_EXTRA_KEY, cryptoAmount)
            if (intent.extras?.getBoolean(REGISTRATION_MODE_EXTRA_KEY) == true) {
                putExtra(PaymentMethodActivity.REGISTRATION_MODE_EXTRA_KEY, true)
            }
        }
        startActivity(intent)
    }
}
