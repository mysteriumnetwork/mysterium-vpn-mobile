package updated.mysterium.vpn.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_item_onboarding.*
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentItemOnboardingBinding
import updated.mysterium.vpn.common.extensions.dpToPx
import updated.mysterium.vpn.model.onboarding.OnboardingScreen
import updated.mysterium.vpn.ui.onboarding.viewpager.ViewPagerActionListener

class OnboardingItemFragment : Fragment(), OnScreenVisibilityChanged {

    private lateinit var binding: FragmentItemOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemOnboardingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<OnboardingScreen>(SCREEN_TAG)?.let { configure(it) }
        bindsAction()
        calculateTabLayoutMargin()
    }

    override fun onScreenBecomeVisible() {
        cardView.setCardBackgroundColor(
            resources.getColor(R.color.onboarding_current_screen_white, null)
        )
    }

    override fun onScreenBecomeFaded() {
        cardView.setCardBackgroundColor(
            resources.getColor(R.color.onboarding_faded_screen_white, null)
        )
    }

    private fun bindsAction() {
        (activity as? ViewPagerActionListener?)?.let { viewPagerAction ->
            nextButton.setOnClickListener { viewPagerAction.onNextClicked() }
            skipButton.setOnClickListener { viewPagerAction.onSkipClicked() }
        }
    }

    private fun configure(onboardingScreen: OnboardingScreen) {
        binding.apply {
            topTitleTextView.text = resources.getString(onboardingScreen.topTitleRes)
            contentTitle.text = resources.getString(onboardingScreen.contentTitleRes)
            contentDescription.text = resources.getString(onboardingScreen.contentDescriptionRes)
            animationView.setAnimation(onboardingScreen.animationRes)
            if (onboardingScreen.additionalTopTitleRes != null) {
                additionalTitleTextView.text = resources.getString(onboardingScreen.additionalTopTitleRes)
            } else {
                additionalTitleTextView.visibility = View.INVISIBLE
            }
        }
    }

    private fun calculateTabLayoutMargin() {
        cardView.measure(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        skipButton.measure(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val cardViewHeight = cardView.measuredHeight
        val skipButtonHeight = skipButton.measuredHeight
        val bottomMargin = requireContext().dpToPx(resources.getDimension(R.dimen.margin_padding_size_large))
        val topMargin = requireContext().dpToPx(resources.getDimension(R.dimen.margin_padding_size_xsmall))
        val totalMargin = cardViewHeight + skipButtonHeight + bottomMargin + topMargin
        (activity as? OnChildViewMeasured?)?.onMarginCalculated(totalMargin)
    }

    companion object {

        fun newInstance(onboardingScreen: OnboardingScreen) = OnboardingItemFragment().apply {
            arguments = Bundle().apply {
                putParcelable(SCREEN_TAG, onboardingScreen)
            }
        }

        private const val SCREEN_TAG = "ONBOARDING_SCREEN_TAG"
    }
}
