package updated.mysterium.vpn.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentItemOnboardingBinding
import updated.mysterium.vpn.model.onboarding.OnboardingScreen
import updated.mysterium.vpn.ui.onboarding.viewpager.ViewPagerActionListener

class OnboardingItemFragment : Fragment(), OnScreenVisibilityChanged {

    companion object {
        fun newInstance(onboardingScreen: OnboardingScreen) = OnboardingItemFragment().apply {
            arguments = Bundle().apply {
                putParcelable(SCREEN_TAG, onboardingScreen)
            }
        }

        private const val SCREEN_TAG = "ONBOARDING_SCREEN_TAG"
    }

    private lateinit var binding: FragmentItemOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<OnboardingScreen>(SCREEN_TAG)?.let { configure(it) }
        bindsAction()
    }

    override fun onScreenBecomeVisible() {
        binding.cardView.setCardBackgroundColor(
            resources.getColor(R.color.onboarding_current_screen_white, null)
        )
    }

    override fun onScreenBecomeFaded() {
        binding.cardView.setCardBackgroundColor(
            resources.getColor(R.color.onboarding_faded_screen_white, null)
        )
    }

    private fun bindsAction() {
        (activity as? ViewPagerActionListener)?.let { viewPagerAction ->
            binding.nextButton.setOnClickListener { viewPagerAction.onNextClicked() }
            binding.skipButton.setOnClickListener { viewPagerAction.onSkipClicked() }
        }
    }

    private fun configure(onboardingScreen: OnboardingScreen) {
        binding.apply {
            topTitleTextView.text = getString(onboardingScreen.topTitleRes)
            contentTitle.text = getString(onboardingScreen.contentTitleRes)
            contentDescription.text = getString(onboardingScreen.contentDescriptionRes)
            animationView.setAnimation(onboardingScreen.animationRes)
            if (onboardingScreen.additionalTopTitleRes != null) {
                additionalTitleTextView.text = getString(onboardingScreen.additionalTopTitleRes)
            } else {
                additionalTitleTextView.visibility = View.INVISIBLE
            }
        }
    }
}
