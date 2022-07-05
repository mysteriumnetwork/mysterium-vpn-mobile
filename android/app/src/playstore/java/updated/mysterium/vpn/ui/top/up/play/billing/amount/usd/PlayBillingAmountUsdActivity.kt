package updated.mysterium.vpn.ui.top.up.play.billing.amount.usd

import android.content.Intent
import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityPlayBillingAmountUsdBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.data.WalletEstimatesUtil
import updated.mysterium.vpn.common.extensions.observeOnce
import updated.mysterium.vpn.model.top.up.TopUpPlayBillingCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingSummaryActivity
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel
import java.util.*

class PlayBillingAmountUsdActivity : BaseActivity() {

    private lateinit var binding: ActivityPlayBillingAmountUsdBinding
    private val viewModel: PlayBillingAmountUsdViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val adapter = PlayBillingAmountUsdAdapter()
    private var selectedItem: TopUpPlayBillingCardItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBillingAmountUsdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    private fun configure() {
        binding.priceRecycler.adapter = adapter
        adapter.onItemSelected = {
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
    }

    private fun setSkuList() {
        viewModel.getSkuDetails().observe(this) { result ->
            result.onSuccess { skuDetailList ->
                skuDetailList.observeOnce(this) { list ->
                    list?.let { adapter.addAll(it) }
                }
            }
        }
    }

    private fun updateWalletEstimates(mystAmount: Double) {
        viewModel.getWalletEquivalent(mystAmount).observe(this) { result ->
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

    private fun onItemSelected(selectedItem: TopUpPlayBillingCardItem) {
        this.selectedItem = selectedItem
        val mystAmount = exchangeRateViewModel.getMystEquivalent(selectedItem.amountUsd)
        updateWalletEstimates(mystAmount)
    }

    private fun navigateToCardPaymentFlow() {
        val intent = Intent(this, PlayBillingSummaryActivity::class.java).apply {
            putExtra(PlayBillingSummaryActivity.SKU_EXTRA_KEY, selectedItem)
        }
        startActivity(intent)
    }

}
