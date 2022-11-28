package updated.mysterium.vpn.ui.top.up.amount.usd

import android.os.Bundle
import network.mysterium.vpn.databinding.ActivityAmountUsdBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.data.WalletEstimatesUtil
import updated.mysterium.vpn.model.top.up.AmountUsdCardItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel
import updated.mysterium.vpn.ui.wallet.WalletViewModel
import java.util.*

abstract class AmountUsdActivity : BaseActivity() {

    companion object {
        const val AMOUNT_USD_EXTRA_KEY = "AMOUNT_USD_EXTRA_KEY"
    }

    val adapter = AmountUsdAdapter()

    private lateinit var binding: ActivityAmountUsdBinding
    private val walletViewModel: WalletViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAmountUsdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    private fun configure() {
        binding.priceRecycler.adapter = adapter
        adapter.onItemSelected = {
            onItemSelected(it)
        }
        populateAdapter(
            onSuccess = { list ->
                list?.let {
                    adapter.replaceAll(list)
                    list.find { item ->
                        item.isSelected
                    }?.amountUsd?.let { amountUsd ->
                        updateWalletEstimates(amountUsd)
                    }
                }
            },
            onFailure = {
                wifiNetworkErrorPopUp {}
            }
        )
    }

    private fun bindsAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.confirmButton.setOnClickListener {
            navigate()
        }
    }

    private fun onItemSelected(selectedItem: AmountUsdCardItem) {
        updateWalletEstimates(selectedItem.amountUsd)
    }

    private fun updateWalletEstimates(amountUsd: Double) {
        val mystAmount = exchangeRateViewModel.getMystEquivalent(amountUsd)
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

    abstract fun populateAdapter(
        onSuccess: (List<AmountUsdCardItem>?) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    abstract fun navigate()

}
