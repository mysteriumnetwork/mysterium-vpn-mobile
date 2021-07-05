package updated.mysterium.vpn.ui.wallet

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import updated.mysterium.vpn.ui.wallet.spendings.SpendingsFragment
import updated.mysterium.vpn.ui.wallet.top.up.TopUpsListFragment

class SelectListPagedAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private companion object {

        // change if new fragment will added
        const val SCREENS_LIST_SIZE = 2
    }

    override fun getItemCount() = SCREENS_LIST_SIZE

    override fun createFragment(position: Int) = getNewFragmentInstance(position)

    private fun getNewFragmentInstance(position: Int) = when (position) {
        0 -> TopUpsListFragment()
        1 -> SpendingsFragment()
        else -> SpendingsFragment() // default, change if new fragment will added
    }
}
