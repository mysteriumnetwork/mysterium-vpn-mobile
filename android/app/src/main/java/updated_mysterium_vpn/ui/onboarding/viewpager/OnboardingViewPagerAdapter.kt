package updated_mysterium_vpn.ui.onboarding.viewpager

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import network.mysterium.vpn.R
import updated_mysterium_vpn.model.onboarding.OnboardingScreen
import updated_mysterium_vpn.ui.onboarding.OnboardingItemFragment

class OnboardingViewPagerAdapter(
        fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    val items = ONBOARDING_SCREENS_LIST

    override fun getItemCount() = ONBOARDING_SCREENS_LIST.size

    override fun createFragment(position: Int) = OnboardingItemFragment.newInstance(items[position])

    companion object {

        private val ONBOARDING_SCREENS_LIST = listOf(
                OnboardingScreen(
                        position = 0,
                        topTitleRes = R.string.onboarding_main_title_1,
                        contentTitleRes = R.string.onboarding_description_title_1,
                        contentDescriptionRes = R.string.onboarding_description_content_1,
                        animationRes = R.raw.onboarding_animation_1
                ),
                OnboardingScreen(
                        position = 1,
                        topTitleRes = R.string.onboarding_main_title_2,
                        contentTitleRes = R.string.onboarding_description_title_2,
                        contentDescriptionRes = R.string.onboarding_description_content_2,
                        animationRes = R.raw.onboarding_animation_2
                ),
                OnboardingScreen(
                        position = 2,
                        topTitleRes = R.string.onboarding_main_title_3,
                        contentTitleRes = R.string.onboarding_description_title_3,
                        contentDescriptionRes = R.string.onboarding_description_content_3,
                        animationRes = R.raw.onboarding_animation_3
                )
        )
    }
}
