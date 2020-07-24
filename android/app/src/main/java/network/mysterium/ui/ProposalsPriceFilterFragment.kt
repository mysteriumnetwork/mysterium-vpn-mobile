package network.mysterium.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import network.mysterium.MainApplication
import network.mysterium.service.core.ProposalPaymentMoney
import network.mysterium.vpn.R

class ProposalsPriceFilterFragment : Fragment() {
    private lateinit var feedbackToolbar: Toolbar
    private lateinit var proposalsViewModel: ProposalsViewModel
    private lateinit var resetBtn: MaterialButton
    private lateinit var applyBtn: MaterialButton
    private lateinit var proposalsPricePerMinuteSlider: Slider
    private lateinit var proposalsPricePerGibSlider: Slider
    private lateinit var proposalsPricePerMinuteValue: TextView
    private lateinit var proposalsPricePerGibValue: TextView
    private val filterSteps: Int = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_proposals_price_filter, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        val appContainer = (requireActivity().application as MainApplication).appContainer
        proposalsViewModel = appContainer.proposalsViewModel

        feedbackToolbar = root.findViewById(R.id.proposals_price_filter_toolbar)
        resetBtn = root.findViewById(R.id.proposals_price_filter_reset_btn)
        applyBtn = root.findViewById(R.id.proposals_price_filter_apply_btn)
        proposalsPricePerMinuteSlider = root.findViewById(R.id.proposals_price_per_minute_slider)
        proposalsPricePerGibSlider = root.findViewById(R.id.proposals_price_per_gib_slider)
        proposalsPricePerMinuteValue = root.findViewById(R.id.proposals_price_per_minute_value)
        proposalsPricePerGibValue = root.findViewById(R.id.proposals_price_per_gib_value)

        val priceSettings = proposalsViewModel.priceSettings

        // Price per minute filter.
        proposalsPricePerMinuteSlider.valueTo = priceSettings.perMinuteMax.toFloat()
        proposalsPricePerMinuteSlider.value = proposalsViewModel.filter.pricePerMinute.toFloat()
        proposalsPricePerMinuteValue.text = formatPriceValue(proposalsViewModel.filter.pricePerMinute)
        proposalsPricePerMinuteSlider.stepSize = (priceSettings.perMinuteMax / filterSteps).toFloat()
        proposalsPricePerMinuteSlider.setLabelFormatter { formatPriceValue(it.toDouble()) }
        proposalsPricePerMinuteSlider.addOnChangeListener { slider, value, fromUser ->
            proposalsViewModel.applyPricePerMinFilter(value.toDouble())
            proposalsPricePerMinuteValue.text = formatPriceValue(proposalsViewModel.filter.pricePerMinute)
        }

        // Price per GiB filter.
        proposalsPricePerGibSlider.valueTo = priceSettings.perGibMax.toFloat()
        proposalsPricePerGibSlider.value = proposalsViewModel.filter.pricePerGiB.toFloat()
        proposalsPricePerGibValue.text = formatPriceValue(proposalsViewModel.filter.pricePerGiB)
        proposalsPricePerGibSlider.stepSize = (priceSettings.perGibMax / filterSteps).toFloat()
        proposalsPricePerGibSlider.setLabelFormatter { formatPriceValue(it.toDouble()) }
        proposalsPricePerGibSlider.addOnChangeListener { slider, value, fromUser ->
            proposalsViewModel.applyPricePerGiBFilter(value.toDouble())
            proposalsPricePerGibValue.text = formatPriceValue(proposalsViewModel.filter.pricePerGiB)
        }

        feedbackToolbar.setNavigationOnClickListener {
            hideKeyboard(root)
            navigateTo(root, Screen.PROPOSALS)
        }

        resetBtn.setOnClickListener {
            proposalsViewModel.applyPricePerMinFilter(proposalsViewModel.priceSettings.defaultPricePerMinute)
            proposalsViewModel.applyPricePerGiBFilter(proposalsViewModel.priceSettings.defaultPricePerGiB)
            navigateTo(root, Screen.PROPOSALS)
        }

        applyBtn.setOnClickListener {
            navigateTo(root, Screen.PROPOSALS)
        }

        onBackPress {
            navigateTo(root, Screen.PROPOSALS)
        }
    }

    private fun formatPriceValue(v: Double): String {
        val price = PriceUtils.displayMoney(ProposalPaymentMoney(amount = v, currency = "MYSTT"))
        return when (v) {
            0.0 -> "free"
            else -> "≤ $price"
        }
    }
}

