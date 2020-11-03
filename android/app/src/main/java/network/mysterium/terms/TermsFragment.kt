/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package network.mysterium.terms

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.MainApplication
import network.mysterium.navigation.emulateHomePress
import network.mysterium.navigation.onBackPress
import network.mysterium.registration.RegistrationViewModel
import network.mysterium.terms.TermsFragmentDirections.Companion.actionTermsFragmentToMainVpnFragment
import network.mysterium.terms.TermsFragmentDirections.Companion.actionTermsFragmentToRegistrationFragment
import network.mysterium.vpn.R

class TermsFragment : Fragment() {
    private lateinit var termsViewModel: TermsViewModel
    private lateinit var registrationViewModel: RegistrationViewModel

    private lateinit var termsTextWiew: TextView
    private lateinit var termsAcceptButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_terms, container, false)

        termsViewModel = (requireActivity().application as MainApplication).appContainer.termsViewModel
        registrationViewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
        termsTextWiew = root.findViewById(R.id.terms_text_wiew)
        termsAcceptButton = root.findViewById(R.id.terms_accept_button)

        termsTextWiew.setText(Html.fromHtml(termsViewModel.termsText, HtmlCompat.FROM_HTML_MODE_LEGACY))

        termsAcceptButton.setOnClickListener {
            termsAcceptButton.isEnabled = false
            CoroutineScope(Dispatchers.Main).launch {
                termsViewModel.acceptCurrentTerms()
                if (registrationViewModel.registered()) {
                    findNavController().navigate(actionTermsFragmentToMainVpnFragment())
                } else {
                    findNavController().navigate(actionTermsFragmentToRegistrationFragment())
                }
            }
        }

        onBackPress { emulateHomePress() }

        return root
    }
}
