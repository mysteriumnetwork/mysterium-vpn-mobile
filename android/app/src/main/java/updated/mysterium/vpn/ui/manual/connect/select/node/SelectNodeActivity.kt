package updated.mysterium.vpn.ui.manual.connect.select.node

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySelectBinding
import network.mysterium.vpn.databinding.ItemTabBinding
import updated.mysterium.vpn.model.manual.connect.TabItemModel

class SelectNodeActivity : AppCompatActivity() {

    private companion object {

        val TAB_ITEMS_CONTENT = listOf(
            TabItemModel(
                textResId = R.string.manual_connect_all_nodes,
                selectedBackgroundResId = R.drawable.shape_all_nodes_selected,
                unselectedBackgroundResId = R.drawable.shape_all_nodes_unselected
            ),
            TabItemModel(
                textResId = R.string.manual_connect_saved_nodes,
                selectedBackgroundResId = R.drawable.shape_saved_nodes_selected,
                unselectedBackgroundResId = R.drawable.shape_saved_nodes_unselected
            )
        )
    }

    private lateinit var binding: ActivitySelectBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindsAction()
        initViewPager()
        iniTabLayout()
    }

    @SuppressLint("InflateParams")
    private fun iniTabLayout() {
        for (index in 0..binding.chooseListTabLayout.tabCount) {
            val tab = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
            val tabBinding = ItemTabBinding.bind(tab)
            tabBinding.allNodesImageButton.text = resources.getString(TAB_ITEMS_CONTENT[index].textResId)
            if (index == 0) {
                tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                    this@SelectNodeActivity,
                    (TAB_ITEMS_CONTENT[index].selectedBackgroundResId)
                )
            } else {
                tabBinding.allNodesImageButton.background = ContextCompat.getDrawable(
                    this@SelectNodeActivity,
                    (TAB_ITEMS_CONTENT[index].unselectedBackgroundResId)
                )
            }
            binding.chooseListTabLayout.getTabAt(index)?.customView = tab
        }
        binding.chooseListTabLayout.addOnTabSelectedListener(

            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.let {
                        ItemTabBinding.bind(it)
                            .allNodesImageButton
                            .background = ContextCompat.getDrawable(
                            this@SelectNodeActivity,
                            (TAB_ITEMS_CONTENT[tab.position].selectedBackgroundResId)
                        )
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.customView?.let {
                        ItemTabBinding.bind(it)
                            .allNodesImageButton
                            .background = ContextCompat.getDrawable(
                            this@SelectNodeActivity,
                            (TAB_ITEMS_CONTENT[tab.position].unselectedBackgroundResId)
                        )

                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // empty
                }
            }
        )
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
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
}
