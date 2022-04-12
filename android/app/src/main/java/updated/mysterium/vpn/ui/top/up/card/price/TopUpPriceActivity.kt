package updated.mysterium.vpn.ui.top.up.card.price

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityTopUpPriceBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.data.WalletEstimatesUtil
import updated.mysterium.vpn.common.extensions.observeOnce
import updated.mysterium.vpn.model.top.up.TopUpPriceCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.TopUpViewModel
import updated.mysterium.vpn.ui.top.up.card.summary.CardSummaryActivity
import updated.mysterium.vpn.ui.top.up.coingate.amount.TopUpAmountViewModel
import updated.mysterium.vpn.ui.top.up.coingate.payment.TopUpPaymentViewModel
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel
import java.util.*

class TopUpPriceActivity : BaseActivity() {

    private lateinit var binding: ActivityTopUpPriceBinding
    private val viewModel: TopUpViewModel by inject()
    private val walletViewModel: TopUpAmountViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val paymentViewModel: TopUpPaymentViewModel by inject()
    private val topUpAdapter = TopUpPriceAdapter()
    private var selectedItem: TopUpPriceCardItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpPriceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    private fun configure() {
        binding.priceRecycler.adapter = topUpAdapter
        topUpAdapter.onItemSelected = {
            onItemSelected(it)
        }
        setSkuList()
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            navigateToCardPaymentFlow()
        }
        binding.freeTrialButtonButton.setOnClickListener {
            viewModel.accountFlowShown()
            navigateToConnectionOrHome(isBackTransition = false)
        }
    }

    private fun setSkuList() {
        paymentViewModel.getSkuDetails().observe(this) {
            it.onSuccess { skuDetailList ->
                skuDetailList.observeOnce(this) {
                    it?.let { topUpAdapter.addAll(it) }
                }
            }
        }
    }

    private fun updateEquivalent(mystAmount: Double) {
        binding.mystEquivalentTextView.text = getString(
            R.string.top_up_myst_equivalent, mystAmount
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
                binding.trafficTopUpItem.setType(
                    WalletEstimatesUtil.convertDownloadType(
                        estimates
                    )
                )
                binding.musicTopUpItem.setData(
                    WalletEstimatesUtil.convertMusicTimeData(
                        estimates
                    )
                )
                binding.musicTopUpItem.setType(
                    WalletEstimatesUtil.convertMusicTimeType(estimates).toUpperCase(Locale.ROOT)
                )
            }
        }
    }

    private fun onItemSelected(selectedItem: TopUpPriceCardItem) {
        this.selectedItem = selectedItem
        val mystAmount = exchangeRateViewModel.getMystEquivalent(selectedItem.price)
        updateEquivalent(mystAmount)
        updateWalletEstimates(mystAmount)
    }

    private fun navigateToCardPaymentFlow() {
        val intent = Intent(this, CardSummaryActivity::class.java).apply {
            putExtra(CardSummaryActivity.SKU_EXTRA_KEY, selectedItem)
        }
        startActivity(intent)
    }
}
