package network.mysterium.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButtonToggleGroup
import network.mysterium.MainApplication
import network.mysterium.registration.RegistrationTopupFragmentDirections.Companion.actionRegistrationTopupFragmentToRegistrationTopupSelectCurrencyFragment
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentRegistrationTopupBinding
import java.math.BigDecimal

class RegistrationTopupFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentRegistrationTopupBinding.inflate(inflater, container, false)
        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.activity

        setupRegistrationTopupAmountToggle(binding.registrationTopupAmountToggle, viewModel.topupAmount)
        binding.registrationTopupContinueButton.setOnClickListener {
            findNavController().navigate(actionRegistrationTopupFragmentToRegistrationTopupSelectCurrencyFragment())
        }

        return binding.root
    }

    private fun setupRegistrationTopupAmountToggle(toggle: MaterialButtonToggleGroup, amount: MutableLiveData<BigDecimal>) {
        toggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.registration_topup_button_10 -> amount.value = BigDecimal(10)
                R.id.registration_topup_button_20 -> amount.value = BigDecimal(20)
                R.id.registration_topup_button_50 -> amount.value = BigDecimal(50)
            }
            if (!isChecked) {
                amount.value = BigDecimal.ZERO
            }
        }
        toggle.check(R.id.registration_topup_button_20)
    }

}
