/*
 * Copyright (C) 2020 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

class WalletFragment : Fragment() {
    private lateinit var walletViewModel: WalletViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var walletBalanceCard: MaterialCardView
    private lateinit var walletBalanceText: TextView
    private lateinit var walletIdentityText: TextView
    private lateinit var walletIdentityRegistrationLayout: ConstraintLayout
    private lateinit var walletIdentityRegistrationLayoutCard: MaterialCardView
    private lateinit var walletIdentityRegistrationLayoutRetryCard: MaterialCardView
    private lateinit var walletIdentityChannelAddressText: TextView
    private lateinit var walletTopUpButton: Button
    private lateinit var walletIdentityRegistrationRetryButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_wallet, container, false)
        walletViewModel = AppContainer.from(activity).walletViewModel

        // Initialize UI elements.
        toolbar = root.findViewById(R.id.wallet_toolbar)
        walletBalanceCard = root.findViewById(R.id.wallet_balance_card)
        walletBalanceText = root.findViewById(R.id.wallet_balance_text)
        walletIdentityText = root.findViewById(R.id.wallet_identity_text)
        walletIdentityRegistrationLayout = root.findViewById(R.id.wallet_identity_registration_layout)
        walletIdentityRegistrationLayoutCard = root.findViewById(R.id.wallet_identity_registration_layout_card)
        walletIdentityRegistrationLayoutRetryCard = root.findViewById(R.id.wallet_identity_registration_layout_retry_card)
        walletIdentityChannelAddressText = root.findViewById(R.id.wallet_identity_channel_address_text)
        walletTopUpButton = root.findViewById(R.id.wallet_topup_button)
        walletIdentityRegistrationRetryButton = root.findViewById(R.id.wallet_identity_registration_retry_button)

        // Handle back press.
        toolbar.setNavigationOnClickListener {
            navigateTo(root, Screen.MAIN)
        }

        onBackPress {
            navigateTo(root, Screen.MAIN)
        }

        walletViewModel.identity.observe(this, Observer {
            handleIdentityChange(it)
        })

        walletViewModel.balance.observe(this, Observer {
            walletBalanceText.text = it.balance.displayValue
        })

        walletTopUpButton.setOnClickListener { handleTopUp(root) }

        walletIdentityChannelAddressText.setOnClickListener { openKovanChannelDetails() }
        walletIdentityText.setOnClickListener { openKovanIdentityDetails() }

        walletIdentityRegistrationRetryButton.setOnClickListener { handleRegistrationRetry() }

        return root
    }

    private fun handleRegistrationRetry() {
        walletIdentityRegistrationRetryButton.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            walletViewModel.loadIdentity {}
            walletIdentityRegistrationRetryButton.isEnabled = true
        }
    }

    private fun openKovanChannelDetails() {
        val channelAddress = walletViewModel.identity.value!!.channelAddress
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://goerli.etherscan.io/address/$channelAddress"))
        startActivity(browserIntent)
    }

    private fun openKovanIdentityDetails() {
        val identityAddress = walletViewModel.identity.value!!.address
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://goerli.etherscan.io/address/$identityAddress"))
        startActivity(browserIntent)
    }

    private fun handleIdentityChange(identity: IdentityModel) {
        walletIdentityText.text = identity.address
        walletIdentityChannelAddressText.text = identity.channelAddress

        if (identity.registered) {
            walletIdentityRegistrationLayout.visibility = View.GONE
            walletBalanceCard.visibility = View.VISIBLE
        } else {
            walletIdentityRegistrationLayout.visibility = View.VISIBLE
            walletIdentityRegistrationLayoutCard.visibility = View.VISIBLE
            walletIdentityRegistrationLayoutRetryCard.visibility = View.GONE
            walletBalanceCard.visibility = View.GONE

            // Show retry button.
            if (identity.registrationFailed) {
                walletIdentityRegistrationLayoutRetryCard.visibility = View.VISIBLE
                walletIdentityRegistrationLayoutCard.visibility = View.GONE
            }
        }
    }

    private fun handleTopUp(root: View) {
        walletTopUpButton.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            walletViewModel.topUp()
            showMessage(root.context, "Balance will be updated in a few moments.")
            walletTopUpButton.isEnabled = true
        }
    }
}
