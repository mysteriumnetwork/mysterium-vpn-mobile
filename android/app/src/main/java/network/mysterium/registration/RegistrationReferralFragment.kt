package network.mysterium.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.MainApplication
import network.mysterium.ui.onChange
import network.mysterium.ui.showMessage
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentRegistrationReferralCodeBinding

class RegistrationReferralFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentRegistrationReferralCodeBinding.inflate(inflater, container, false)
        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.activity

        binding.registrationReferralTokenEdit.onChange { token ->
            viewModel.token.value = token
        }
        binding.registrationReferralRegisterButton.setOnClickListener {
            binding.registrationReferralTokenEdit.clearFocus()
            val inputMethodManager = context?.getSystemService(InputMethodManager::class.java)
            inputMethodManager?.hideSoftInputFromWindow(binding.registrationReferralTokenEdit.windowToken, 0)
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    viewModel.registerWithReferralToken()
                } catch (e: Exception) {
                    showMessage(binding.root.context, getString(R.string.wallet_registration_failed))
                }
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
    }

}
