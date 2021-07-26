package updated.mysterium.vpn.ui.wallet

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import mysterium.Estimates
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityWalletBinding
import network.mysterium.vpn.databinding.ItemTabBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.data.WalletEstimatesUtil
import updated.mysterium.vpn.common.tab.layout.StateTabSelectedListener
import updated.mysterium.vpn.model.manual.connect.OnboardingTabItem
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

class WalletActivity : BaseActivity() {

    private companion object {
        val TAB_ITEMS_CONTENT = listOf(
            OnboardingTabItem(
                textResId = R.string.wallet_top_up_label,
                selectedBackgroundResId = R.drawable.shape_all_nodes_selected,
                unselectedBackgroundResId = R.drawable.shape_all_nodes_unselected,
                rtlSelectedBackgroundResId = R.drawable.shape_saved_nodes_selected,
                rtlUnselectedBackgroundResId = R.drawable.shape_saved_nodes_unselected
            ),
            OnboardingTabItem(
                textResId = R.string.wallet_spedings_label,
                selectedBackgroundResId = R.drawable.shape_saved_nodes_selected,
                unselectedBackgroundResId = R.drawable.shape_saved_nodes_unselected,
                rtlSelectedBackgroundResId = R.drawable.shape_all_nodes_selected,
                rtlUnselectedBackgroundResId = R.drawable.shape_all_nodes_unselected
            )
        )
    }

    private lateinit var binding: ActivityWalletBinding
    private lateinit var viewPager: ViewPager2
    private val balanceViewModel: BalanceViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val viewModel: WalletViewModel by inject()
    private var firstTabBinding: ItemTabBinding? = null
    private var secondTabBinding: ItemTabBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
        balanceViewModel.getCurrentBalance()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun configure() {
        balanceViewModel.balanceLiveData.value?.let {
            handleBalance(it)
        }
        initToolbar(binding.manualConnectToolbar)
        initViewPager()
        initTabLayout(resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL)
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this, {
            handleBalance(it)
        })
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val transitionAnimation = ActivityOptions.makeCustomAnimation(
                applicationContext,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ).toBundle()
            startActivity(intent, transitionAnimation)
        }
        binding.topUpButton.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
        }
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionOrHome()
        }
    }

    private fun handleBalance(balance: Double) {
        binding.balanceTextView.text = getString(R.string.wallet_current_balance, balance)
        getWalletEquivalent(balance)
        getUsdEquivalent(balance)
    }

    @SuppressLint("InflateParams")
    private fun initTabLayout(isRTL: Boolean) {
        for (index in 0 until binding.chooseListTabLayout.tabCount) {
            val tab = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
            val tabBinding = ItemTabBinding.bind(tab)
            tabBinding.allNodesImageButton.text =
                resources.getString(TAB_ITEMS_CONTENT[index].textResId)
            if (index == 0) {
                if (isRTL) {
                    tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                        this@WalletActivity,
                        (TAB_ITEMS_CONTENT[index].rtlSelectedBackgroundResId)
                    )
                } else {
                    tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                        this@WalletActivity,
                        (TAB_ITEMS_CONTENT[index].selectedBackgroundResId)
                    )
                }
                firstTabBinding = tabBinding
            } else {
                if (isRTL) {
                    tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                        this@WalletActivity,
                        (TAB_ITEMS_CONTENT[index].rtlUnselectedBackgroundResId)
                    )
                } else {
                    tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                        this@WalletActivity,
                        (TAB_ITEMS_CONTENT[index].unselectedBackgroundResId)
                    )
                }
                secondTabBinding = tabBinding
            }
            binding.chooseListTabLayout.getTabAt(index)?.customView = tab
        }
        binding.chooseListTabLayout.addOnTabSelectedListener(

            object : StateTabSelectedListener() {

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.let {
                        val backgroundRes = if (isRTL) {
                            (TAB_ITEMS_CONTENT[tab.position].rtlSelectedBackgroundResId)
                        } else {
                            (TAB_ITEMS_CONTENT[tab.position].selectedBackgroundResId)
                        }
                        ItemTabBinding.bind(it)
                            .allNodesImageButton
                            .background = ContextCompat.getDrawable(
                            this@WalletActivity,
                            backgroundRes
                        )
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.customView?.let {
                        val backgroundRes = if (isRTL) {
                            (TAB_ITEMS_CONTENT[tab.position].rtlUnselectedBackgroundResId)
                        } else {
                            (TAB_ITEMS_CONTENT[tab.position].unselectedBackgroundResId)
                        }
                        ItemTabBinding.bind(it)
                            .allNodesImageButton
                            .background = ContextCompat.getDrawable(
                            this@WalletActivity,
                            backgroundRes
                        )
                    }
                }
            }
        )
        binding.chooseListTabLayout.getTabAt(1)?.select()
    }

    private fun initViewPager() {
        val listsViewPagerAdapter = SelectListPagedAdapter(this)
        binding.listViewPager.apply {
            viewPager = this
            adapter = listsViewPagerAdapter
        }
        attachTabLayout()
    }

    private fun attachTabLayout() {
        TabLayoutMediator(binding.chooseListTabLayout, viewPager) { tab, _ ->
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun getUsdEquivalent(balance: Double) {
        binding.usdEquivalentTextView.text = getString(
            R.string.wallet_usd_equivalent, exchangeRateViewModel.usdEquivalent * balance
        )
    }

    private fun getWalletEquivalent(balance: Double) {
        viewModel.getWalletEquivalent(balance).observe(this, { result ->
            result.onSuccess { estimates ->
                inflateVideoEstimate(estimates)
                inflateWebEstimate(estimates)
                inflateDownloadEstimate(estimates)
                binding.music.setData(WalletEstimatesUtil.convertMusicCount(estimates))
            }
        })
    }

    private fun inflateVideoEstimate(estimates: Estimates) {
        val data = WalletEstimatesUtil.convertVideoData(estimates)
        val type = WalletEstimatesUtil.convertVideoType(estimates)
        binding.video.setData(data + type)
    }

    private fun inflateWebEstimate(estimates: Estimates) {
        val data = WalletEstimatesUtil.convertWebData(estimates)
        val type = WalletEstimatesUtil.convertWebType(estimates)
        binding.browsing.setData(data + type)
    }

    private fun inflateDownloadEstimate(estimates: Estimates) {
        val data = WalletEstimatesUtil.convertDownloadData(estimates)
        val type = WalletEstimatesUtil.convertDownloadType(estimates)
        binding.download.setData("$data$type")
    }
}
