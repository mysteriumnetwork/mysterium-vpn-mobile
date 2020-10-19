package network.mysterium.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import network.mysterium.MainApplication
import network.mysterium.vpn.R

class RegistrationReferralFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration_referral_code, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
    }

}
