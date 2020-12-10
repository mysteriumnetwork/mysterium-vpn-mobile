package network.mysterium.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import network.mysterium.registration.RegistrationDoneFragmentDirections.Companion.actionRegistrationDoneFragmentToMainVpnFragment
import network.mysterium.vpn.R

class RegistrationDoneFragment : Fragment() {

    private lateinit var continueButton : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_registration_done, container, false)

        continueButton = root.findViewById(R.id.registration_done_continue_button)
        continueButton.setOnClickListener {
            findNavController().navigate(actionRegistrationDoneFragmentToMainVpnFragment())
        }

        return root
    }

}
