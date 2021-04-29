package updated.mysterium.vpn.ui.manual.connect.select.node

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySelectBinding
import network.mysterium.vpn.databinding.ItemTabBinding
import updated.mysterium.vpn.common.tab.layout.StateTabSelectedListener
import updated.mysterium.vpn.model.manual.connect.OnboardingTabItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.manual.connect.search.SearchActivity

class SelectNodeActivity : BaseActivity() {

    private companion object {

        val TAB_ITEMS_CONTENT = listOf(
            OnboardingTabItem(
                textResId = R.string.manual_connect_all_nodes,
                selectedBackgroundResId = R.drawable.shape_all_nodes_selected,
                unselectedBackgroundResId = R.drawable.shape_all_nodes_unselected,
                rtlSelectedBackgroundResId = R.drawable.shape_saved_nodes_selected,
                rtlUnselectedBackgroundResId = R.drawable.shape_saved_nodes_unselected
            ),
            OnboardingTabItem(
                textResId = R.string.manual_connect_saved_nodes,
                selectedBackgroundResId = R.drawable.shape_saved_nodes_selected,
                unselectedBackgroundResId = R.drawable.shape_saved_nodes_unselected,
                rtlSelectedBackgroundResId = R.drawable.shape_all_nodes_selected,
                rtlUnselectedBackgroundResId = R.drawable.shape_all_nodes_unselected
            )
        )
    }

    private lateinit var binding: ActivitySelectBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        initViewPager()
        initTabLayout(resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL)
    }

    @SuppressLint("InflateParams")
    private fun initTabLayout(isRTL: Boolean) {
        for (index in 0 until binding.chooseListTabLayout.tabCount) {
            val tab = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
            val tabBinding = ItemTabBinding.bind(tab)
            tabBinding.allNodesImageButton.text = resources.getString(TAB_ITEMS_CONTENT[index].textResId)
            if (index == 0) {
                if (isRTL) {
                    tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                        this@SelectNodeActivity,
                        (TAB_ITEMS_CONTENT[index].rtlSelectedBackgroundResId)
                    )
                } else {
                    tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                        this@SelectNodeActivity,
                        (TAB_ITEMS_CONTENT[index].selectedBackgroundResId)
                    )
                }
            } else {
                if (isRTL) {
                    tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                        this@SelectNodeActivity,
                        (TAB_ITEMS_CONTENT[index].rtlUnselectedBackgroundResId)
                    )
                } else {
                    tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                        this@SelectNodeActivity,
                        (TAB_ITEMS_CONTENT[index].unselectedBackgroundResId)
                    )
                }
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
                            this@SelectNodeActivity,
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
                            this@SelectNodeActivity,
                            backgroundRes
                        )

                    }
                }
            }
        )
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            navigateToSearch()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    private fun initViewPager() {
        val nodesViewPagerAdapter = SelectNodePagerAdapter(this)
        binding.nodesListViewPager.apply {
            viewPager = this
            adapter = nodesViewPagerAdapter
        }
        attachTabLayout()
    }

    private fun attachTabLayout() {
        TabLayoutMediator(binding.chooseListTabLayout, viewPager) { tab, _ ->
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun navigateToSearch() {
        startActivity(Intent(this, SearchActivity::class.java))
    }
}
