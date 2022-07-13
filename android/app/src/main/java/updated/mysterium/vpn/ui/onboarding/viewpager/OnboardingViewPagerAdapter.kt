package updated.mysterium.vpn.ui.onboarding.viewpager

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import updated.mysterium.vpn.common.Flavors
import updated.mysterium.vpn.model.onboarding.OnboardingScreen
import updated.mysterium.vpn.ui.onboarding.OnboardingItemFragment

class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private companion object {

        private val decentralizedScreen = OnboardingScreen(
            topTitleRes = R.string.onboarding_main_title_1,
            contentTitleRes = R.string.onboarding_description_title_1,
            contentDescriptionRes = R.string.onboarding_description_content_1,
            animationRes = R.raw.onboarding_animation_1
        )

        private val noSubscriptionScreen = OnboardingScreen(
            topTitleRes = R.string.onboarding_main_title_2,
            contentTitleRes = R.string.onboarding_description_title_2,
            contentDescriptionRes = R.string.onboarding_description_content_2,
            animationRes = R.raw.onboarding_animation_2
        )

        private val cryptoScreen = OnboardingScreen(
            topTitleRes = R.string.onboarding_main_title_3,
            additionalTopTitleRes = R.string.manual_connect_currency_hint,
            contentTitleRes = R.string.onboarding_description_title_3,
            contentDescriptionRes = R.string.onboarding_description_content_3,
            animationRes = R.raw.onboarding_animation_3
        )

        private val smartConnectScreen = OnboardingScreen(
            topTitleRes = R.string.onboarding_main_title_4,
            contentTitleRes = R.string.onboarding_description_title_4,
            contentDescriptionRes = R.string.onboarding_description_content_4,
            animationRes = R.raw.onboarding_animation_4
        )

        val ONBOARDING_SCREENS_LIST =
            if (BuildConfig.FLAVOR == Flavors.PLAY_STORE.value) {
                listOf(
                    decentralizedScreen,
                    noSubscriptionScreen,
                    smartConnectScreen
                )
            } else {
                listOf(
                    decentralizedScreen,
                    noSubscriptionScreen,
                    cryptoScreen,
                    smartConnectScreen
                )
            }
    }

    val items = ONBOARDING_SCREENS_LIST

    override fun getItemCount() = ONBOARDING_SCREENS_LIST.size

    override fun createFragment(position: Int) = OnboardingItemFragment.newInstance(items[position])
}
