
package updated.mysterium.vpn.ui.wallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityWalletBinding
import network.mysterium.vpn.databinding.ItemTabBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.tab.layout.StateTabSelectedListener
import updated.mysterium.vpn.model.manual.connect.OnboardingTabItem
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

class WalletActivity : BaseActivity() {

    private companion object {
        const val TAG = "WalletActivity"
        val TAB_ITEMS_CONTENT = listOf(
            OnboardingTabItem(
                textResId = R.string.wallet_top_up_label,
                selectedBackgroundResId = R.drawable.shape_all_nodes_selected,
                unselectedBackgroundResId = R.drawable.shape_all_nodes_unselected
            ),
            OnboardingTabItem(
                textResId = R.string.wallet_spedings_label,
                selectedBackgroundResId = R.drawable.shape_saved_nodes_selected,
                unselectedBackgroundResId = R.drawable.shape_saved_nodes_unselected
            )
        )
    }

    private lateinit var binding: ActivityWalletBinding
    private lateinit var viewPager: ViewPager2
    private val balanceViewModel: BalanceViewModel by inject()
    private val viewModel: WalletViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
        balanceViewModel.getCurrentBalance()
    }

    private fun configure() {
        initViewPager()
        initTabLayout()
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this, {
            binding.balanceTextView.text = getString(R.string.wallet_current_balance, it)
            binding.manualConnectToolbar.setBalance(it)
            getUsdEquivalent(it)
        })
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
        binding.topUpButton.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
        }
    }

    @SuppressLint("InflateParams")
    private fun initTabLayout() {
        for (index in 0 until binding.chooseListTabLayout.tabCount) {
            val tab = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
            val tabBinding = ItemTabBinding.bind(tab)
            tabBinding.allNodesImageButton.text = resources.getString(TAB_ITEMS_CONTENT[index].textResId)
            if (index == 0) {
                tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                    this@WalletActivity,
                    (TAB_ITEMS_CONTENT[index].selectedBackgroundResId)
                )
            } else {
                tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                    this@WalletActivity,
                    (TAB_ITEMS_CONTENT[index].unselectedBackgroundResId)
                )
            }
            binding.chooseListTabLayout.getTabAt(index)?.customView = tab
        }
        binding.chooseListTabLayout.addOnTabSelectedListener(

            object : StateTabSelectedListener() {

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.let {
                        ItemTabBinding.bind(it)
                            .allNodesImageButton
                            .background = ContextCompat.getDrawable(
                            this@WalletActivity,
                            (TAB_ITEMS_CONTENT[tab.position].selectedBackgroundResId)
                        )
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.customView?.let {
                        ItemTabBinding.bind(it)
                            .allNodesImageButton
                            .background = ContextCompat.getDrawable(
                            this@WalletActivity,
                            (TAB_ITEMS_CONTENT[tab.position].unselectedBackgroundResId)
                        )

                    }
                }
            }
        )
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
        viewModel.getUsdEquivalent(balance).observe(this, { result ->
            result.onSuccess {
                binding.usdEquivalentTextView.text = getString(R.string.wallet_usd_equivalent, it)
            }
            result.onFailure {
                Log.e(TAG, "Getting exchange rate failed")
                // TODO("Implement error handling")
            }
        })
    }
}
