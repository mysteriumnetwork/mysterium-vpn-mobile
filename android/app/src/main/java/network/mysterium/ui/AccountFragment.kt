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

package network.mysterium.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.AppContainer
import network.mysterium.vpn.R
import android.content.Intent
import android.net.Uri


class AccountFragment : Fragment() {
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var accountBalanceCard: MaterialCardView
    private lateinit var accountBalanceText: TextView
    private lateinit var accountIdentityText: TextView
    private lateinit var accountIdentityRegistrationLayout: ConstraintLayout
    private lateinit var accountIdentityChannelAddressText: TextView
    private lateinit var accountTopUpButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_account, container, false)
        accountViewModel = AppContainer.from(activity).accountViewModel

        // Initialize UI elements.
        toolbar = root.findViewById(R.id.account_toolbar)
        accountBalanceCard = root.findViewById(R.id.account_balance_card)
        accountBalanceText = root.findViewById(R.id.account_balance_text)
        accountIdentityText = root.findViewById(R.id.account_identity_text)
        accountIdentityRegistrationLayout = root.findViewById(R.id.account_identity_registration_layout)
        accountIdentityChannelAddressText = root.findViewById(R.id.account_identity_channel_address_text)
        accountTopUpButton = root.findViewById(R.id.account_topup_button)

        // Handle back press.
        toolbar.setNavigationOnClickListener {
            navigateTo(root, Screen.MAIN)
        }

        onBackPress {
            navigateTo(root, Screen.MAIN)
        }

        accountViewModel.identity.observe(this, Observer {
            handleIdentityChange(it)
        })

        accountViewModel.balance.observe(this, Observer {
            accountBalanceText.text = it.value.displayValue
        })

        accountTopUpButton.setOnClickListener { handleTopUp(root) }

        accountIdentityChannelAddressText.setOnClickListener { openKovanChannelDetails() }

        return root
    }

    private fun openKovanChannelDetails() {
        val channelAddress = accountViewModel.identity.value!!.channelAddress
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kovan.etherscan.io/address/$channelAddress"))
        startActivity(browserIntent)
    }

    private fun handleIdentityChange(it: IdentityModel) {
        accountIdentityText.text = it.address
        accountIdentityChannelAddressText.text = it.channelAddress

        if (it.registered) {
            accountIdentityRegistrationLayout.visibility = View.GONE
            accountBalanceCard.visibility = View.VISIBLE
        } else {
            accountIdentityRegistrationLayout.visibility = View.VISIBLE
            accountBalanceCard.visibility = View.GONE
        }
    }

    private fun handleTopUp(root: View) {
        accountTopUpButton.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            accountViewModel.topUp()
            showMessage(root.context, "Balance will be updated in a few moments.")
            accountTopUpButton.isEnabled = true
        }
    }
}
