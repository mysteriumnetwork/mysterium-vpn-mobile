package updated.mysterium.vpn.ui.onboarding.viewpager

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import network.mysterium.vpn.R
import updated.mysterium.vpn.model.onboarding.OnboardingScreen
import updated.mysterium.vpn.ui.onboarding.OnboardingItemFragment

class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private companion object {

        val ONBOARDING_SCREENS_LIST = listOf(
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
                additionalTopTitleRes = R.string.manual_connect_currency_hint,
                contentTitleRes = R.string.onboarding_description_title_3,
                contentDescriptionRes = R.string.onboarding_description_content_3,
                animationRes = R.raw.onboarding_animation_3
            ),
            OnboardingScreen(
                position = 3,
                topTitleRes = R.string.onboarding_main_title_4,
                contentTitleRes = R.string.onboarding_description_title_4,
                contentDescriptionRes = R.string.onboarding_description_content_4,
                animationRes = R.raw.onboarding_animation_4
            )
        )
    }

    val items = ONBOARDING_SCREENS_LIST

    override fun getItemCount() = ONBOARDING_SCREENS_LIST.size

    override fun createFragment(position: Int) = OnboardingItemFragment.newInstance(items[position])
}
