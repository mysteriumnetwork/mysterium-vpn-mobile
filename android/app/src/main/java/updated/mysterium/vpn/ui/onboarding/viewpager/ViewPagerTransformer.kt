package updated.mysterium.vpn.ui.onboarding.viewpager

import android.view.View
import androidx.annotation.DimenRes
import androidx.viewpager2.widget.ViewPager2
import network.mysterium.vpn.R

class ViewPagerTransformer(
    @DimenRes private val nextItemVisible: Int = R.dimen.viewpager_next_item_visible,
    @DimenRes private val currentItemMargin: Int = R.dimen.viewpager_current_item_margin
) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val nextItemVisiblePx = page
            .context
            .resources
            .getDimension(nextItemVisible)
        val currentItemMarginPx = page
            .context
            .resources
            .getDimension(currentItemMargin)
        val pageTranslationX = nextItemVisiblePx + currentItemMarginPx
        page.translationX = -pageTranslationX * position
    }
}
