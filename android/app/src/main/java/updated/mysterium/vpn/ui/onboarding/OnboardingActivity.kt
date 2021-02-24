package updated.mysterium.vpn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_onboarding.*
import network.mysterium.vpn.databinding.ActivityOnboardingBinding
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.onboarding.viewpager.*

class OnboardingActivity : AppCompatActivity(), ViewPagerActionListener, OnChildViewMeasured {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCustomViewPagerBehaviour()
    }

    override fun onNextClicked() {
        if (viewPager.currentItem + 1 == viewPager.adapter?.itemCount) {
            skipOnboarding()
        } else {
            viewPager.currentItem++
        }
    }

    override fun onSkipClicked() {
        skipOnboarding()
    }

    override fun onMarginCalculated(margin: Int) {
        val layoutParams = tabLayoutDots.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = margin
        tabLayoutDots.requestLayout()
    }

    private fun initCustomViewPagerBehaviour() {
        val onboardingAdapter = OnboardingViewPagerAdapter(this)
        binding.descriptionsViewPager.apply {
            viewPager = this
            offscreenPageLimit = 1
            adapter = onboardingAdapter
            setPageTransformer(ViewPagerTransformer())
            addItemDecoration(ViewPagerItemDecoration())
            registerOnPageChangeCallback(ViewPagerPageCallback(
                lastIndex = onboardingAdapter.items.lastIndex,
                onPageSelected = { showVisibleOnboardingScreen(it) },
                viewPagerFinished = { skipOnboarding() }
            ))
        }
        attachTabLayout()
    }

    private fun attachTabLayout() {
        TabLayoutMediator(binding.tabLayoutDots, viewPager) { tab, _ ->
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun showVisibleOnboardingScreen(screenIndex: Int) {
        val visibleFragment = supportFragmentManager.findFragmentByTag("f$screenIndex")
        (visibleFragment as? OnScreenVisibilityChanged?)?.onScreenBecomeVisible()
        val previousFragment = supportFragmentManager.findFragmentByTag("f${screenIndex - 1}")
        (previousFragment as? OnScreenVisibilityChanged?)?.onScreenBecomeFaded()
        val nextFragment = supportFragmentManager.findFragmentByTag("f${screenIndex + 1}")
        (nextFragment as? OnScreenVisibilityChanged?)?.onScreenBecomeFaded()
    }

    private fun skipOnboarding() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
