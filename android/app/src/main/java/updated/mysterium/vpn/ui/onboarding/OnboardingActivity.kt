package updated.mysterium.vpn.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import network.mysterium.vpn.databinding.ActivityOnboardingBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.onboarding.viewpager.*
import updated.mysterium.vpn.ui.terms.TermsOfUseActivity
import androidx.core.view.WindowCompat
import updated.mysterium.vpn.ui.base.BaseActivity

class OnboardingActivity : BaseActivity(), ViewPagerActionListener {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2
    private val viewModel: OnboardingViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets(binding.root)
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
        viewModel.userLoggedIn()
        if (viewModel.isTermsAccepted()) {
            startActivity(Intent(this, HomeSelectionActivity::class.java))
        } else {
            startActivity(Intent(this, TermsOfUseActivity::class.java))
        }
        finish()
    }
}
