package updated_mysterium_vpn.ui.onboarding.viewpager

import androidx.viewpager2.widget.ViewPager2

class ViewPagerPageCallback(
        private val lastIndex: Int,
        private val onPageSelected: (Int) -> Unit,
        private val viewPagerFinished: () -> Unit
) : ViewPager2.OnPageChangeCallback() {

    private var isLastPageSwiped = false
    private var pagesScrolledCounter = 0

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (position == lastIndex && positionOffset.toInt() == 0 && !isLastPageSwiped) {
            if (pagesScrolledCounter != 0) {
                isLastPageSwiped = true
                viewPagerFinished.invoke()
            }
            pagesScrolledCounter++
        } else {
            pagesScrolledCounter = 0
        }
    }

    override fun onPageSelected(position: Int) {
        onPageSelected.invoke(position)
    }
}
