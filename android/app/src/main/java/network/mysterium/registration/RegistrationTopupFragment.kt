package network.mysterium.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButtonToggleGroup
import network.mysterium.MainApplication
import network.mysterium.registration.RegistrationTopupFragmentDirections.Companion.actionRegistrationTopupFragmentToRegistrationTopupPaymentFragment
import network.mysterium.ui.IdentityRegistrationStatus
import network.mysterium.vpn.R
import java.math.BigDecimal

class RegistrationTopupFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var continueButton: Button
    private lateinit var topupAmountToggle: MaterialButtonToggleGroup
    private lateinit var topupAmountDisplay: TextView
    private lateinit var registrationFeeDisplay: TextView
    private lateinit var totalAmountDisplay: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_registration_topup, container, false)
        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel

        val nav = findNavController()
        continueButton = root.findViewById(R.id.registration_topup_continue_button)
        continueButton.setOnClickListener {
            nav.navigate(actionRegistrationTopupFragmentToRegistrationTopupPaymentFragment())
        }

        topupAmountToggle = root.findViewById(R.id.registration_topup_amount_toggle)
        topupAmountToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.registration_topup_button_10 -> viewModel.topupAmount.value = BigDecimal(10)
                R.id.registration_topup_button_20 -> viewModel.topupAmount.value = BigDecimal(20)
                R.id.registration_topup_button_50 -> viewModel.topupAmount.value = BigDecimal(50)
            }
            if (!isChecked) {
                viewModel.topupAmount.value = BigDecimal.ZERO
            }
        }

        topupAmountDisplay = root.findViewById(R.id.registration_topup_amount_display)
        viewModel.topupAmount.observe(viewLifecycleOwner, Observer { amt ->
            topupAmountDisplay.text = getString(R.string.mystt_display, amt)
        })

        registrationFeeDisplay = root.findViewById(R.id.registration_topup_registration_fee_display)
        viewModel.registrationFee.observe(viewLifecycleOwner, Observer { amt ->
            registrationFeeDisplay.text = getString(R.string.mystt_display, amt)
        })

        totalAmountDisplay = root.findViewById(R.id.registration_topup_total_amount_display)
        viewModel.totalAmount.observe(viewLifecycleOwner, Observer { amt ->
            totalAmountDisplay.text = getString(R.string.mystt_display, amt)
        })

        topupAmountToggle.check(R.id.registration_topup_button_20)

        return root
    }

}
