package updated.mysterium.vpn.ui.manual.connect.select.node

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import updated.mysterium.vpn.ui.manual.connect.select.node.all.AllNodesFragment
import updated.mysterium.vpn.ui.manual.connect.select.node.saved.SavedNodesFragment

class NodesViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private companion object {

        val NODES_SCREENS_LIST = listOf(AllNodesFragment(), SavedNodesFragment())
    }

    override fun getItemCount() = NODES_SCREENS_LIST.size

    override fun createFragment(position: Int) = NODES_SCREENS_LIST[position]
}
