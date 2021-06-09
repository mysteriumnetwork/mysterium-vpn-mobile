package updated.mysterium.vpn.ui.wallet

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import updated.mysterium.vpn.ui.wallet.spendings.SpendingsFragment
import updated.mysterium.vpn.ui.wallet.top.up.TopUpsListFragment

class SelectListPagedAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private companion object {

        val SCREENS_LIST = listOf(TopUpsListFragment(), SpendingsFragment())
    }

    override fun getItemCount() = SCREENS_LIST.size

    override fun createFragment(position: Int) = SCREENS_LIST[position]
}
