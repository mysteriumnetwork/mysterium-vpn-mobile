package updated_mysterium_vpn.ui.onboarding.viewpager

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R

class ViewPagerItemDecoration(
        @DimenRes private val horizontalMarginInDp: Int = R.dimen.viewpager_current_item_margin
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        outRect.right = view.context.resources.getDimension(horizontalMarginInDp).toInt()
        outRect.left = view.context.resources.getDimension(horizontalMarginInDp).toInt()
    }
}
