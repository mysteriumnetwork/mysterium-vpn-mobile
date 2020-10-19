package network.mysterium.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import network.mysterium.MainApplication
import network.mysterium.registration.RegistrationFragmentDirections.Companion.actionRegistrationFragmentToReferralCodeRegistrationFragment
import network.mysterium.registration.RegistrationFragmentDirections.Companion.actionRegistrationFragmentToTopupRegistrationFragment
import network.mysterium.vpn.R

class RegistrationFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel

    private lateinit var getStartedButton: Button
    private lateinit var useReferralCodeButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_registration_start, container, false)
        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
        val nav = findNavController()
        getStartedButton = root.findViewById(R.id.registration_get_started_button)
        getStartedButton.setOnClickListener {
            nav.navigate(actionRegistrationFragmentToTopupRegistrationFragment())
        }
        useReferralCodeButton = root.findViewById(R.id.registration_use_referral_code_button)
        useReferralCodeButton.setOnClickListener {
            nav.navigate(actionRegistrationFragmentToReferralCodeRegistrationFragment())
        }

        viewModel.identity.observe(viewLifecycleOwner, Observer {
            it.status
        })
        return root
    }

}
