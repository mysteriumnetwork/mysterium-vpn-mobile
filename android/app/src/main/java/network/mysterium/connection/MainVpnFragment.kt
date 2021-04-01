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

package network.mysterium.connection

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import network.mysterium.AppContainer
import network.mysterium.navigation.Screen
import network.mysterium.navigation.emulateHomePress
import network.mysterium.navigation.navigateTo
import network.mysterium.navigation.onBackPress
import network.mysterium.proposal.ProposalViewItem
import network.mysterium.proposal.ProposalsViewModel
import network.mysterium.service.core.ConnectInsufficientBalanceException
import network.mysterium.service.core.ConnectInvalidProposalException
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.service.core.ProposalPaymentMoney
import network.mysterium.ui.*
import network.mysterium.vpn.R
import network.mysterium.wallet.BalanceModel
import network.mysterium.wallet.WalletViewModel

class MainVpnFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var proposalsViewModel: ProposalsViewModel
    private lateinit var walletViewModel: WalletViewModel

    private var job: Job? = null
    private lateinit var connStatusLabel: TextView
    private lateinit var conStatusIP: TextView
    private lateinit var vpnStatusCountry: ImageView
    private lateinit var selectProposalLayout: ConstraintLayout
    private lateinit var feedbackButton: ImageView
    private lateinit var vpnSelectedProposalCountryLabel: TextView
    private lateinit var vpnSelectedProposalProviderLabel: TextView
    private lateinit var vpnSelectedProposalCountryIcon: ImageView
    private lateinit var vpnProposalPickerFavoriteLayput: RelativeLayout
    private lateinit var vpnProposalPickerFavoriteImage: ImageView
    private lateinit var connectionButton: TextView
    private lateinit var vpnStatsDurationLabel: TextView
    private lateinit var vpnStatsBytesSentLabel: TextView
    private lateinit var vpnStatsBytesReceivedLabel: TextView
    private lateinit var vpnStatsBytesReceivedUnits: TextView
    private lateinit var vpnStatsBytesSentUnits: TextView
    private lateinit var vpnStatsPaid: TextView
    private lateinit var vpnAccountBalanceLabel: TextView
    private lateinit var vpnAccountBalanceLayout: LinearLayout
    private lateinit var vpnStatsLayout: ConstraintLayout
    private lateinit var deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val appContainer = AppContainer.from(activity)
        sharedViewModel = appContainer.sharedViewModel
        proposalsViewModel = appContainer.proposalsViewModel
        walletViewModel = appContainer.walletViewModel
        deferredMysteriumCoreService = appContainer.deferredMysteriumCoreService

        val root = inflater.inflate(R.layout.fragment_main_vpn, container, false)

        // Initialize UI elements.
        connStatusLabel = root.findViewById(R.id.vpn_status_label)
        conStatusIP = root.findViewById(R.id.vpn_status_ip)
        vpnStatusCountry = root.findViewById(R.id.vpn_status_country)
        selectProposalLayout = root.findViewById(R.id.vpn_select_proposal_layout)
        feedbackButton = root.findViewById(R.id.vpn_feedback_button)
        vpnSelectedProposalCountryLabel = root.findViewById(R.id.vpn_selected_proposal_country_label)
        vpnSelectedProposalProviderLabel = root.findViewById(R.id.vpn_selected_proposal_provider_label)
        vpnSelectedProposalCountryIcon = root.findViewById(R.id.vpn_selected_proposal_country_icon)
        vpnProposalPickerFavoriteLayput = root.findViewById(R.id.vpn_proposal_picker_favorite_layout)
        vpnProposalPickerFavoriteImage = root.findViewById(R.id.vpn_proposal_picker_favorite_image)
        connectionButton = root.findViewById(R.id.vpn_connection_button)
        vpnStatsDurationLabel = root.findViewById(R.id.vpn_stats_duration)
        vpnStatsBytesReceivedLabel = root.findViewById(R.id.vpn_stats_bytes_received)
        vpnStatsBytesSentLabel = root.findViewById(R.id.vpn_stats_bytes_sent)
        vpnStatsBytesReceivedUnits = root.findViewById(R.id.vpn_stats_bytes_received_units)
        vpnStatsBytesSentUnits = root.findViewById(R.id.vpn_stats_bytes_sent_units)
        vpnStatsPaid = root.findViewById(R.id.vpn_stats_paid)
        vpnAccountBalanceLabel = root.findViewById(R.id.vpn_account_balance_label)
        vpnAccountBalanceLayout = root.findViewById(R.id.vpn_account_balance_layout)
        vpnStatsLayout = root.findViewById(R.id.vpn_stats_layout)

        // TODO: Hide
        // vpnStatsLayout.visibility = View.INVISIBLE

        feedbackButton.setOnClickListener {
            val activity = this.activity
            if (activity is AppCompatActivity) {
                val drawer = activity.findViewById<DrawerLayout>(R.id.drawer_layout)
                drawer?.openDrawer(GravityCompat.START)
            }
        }

        vpnAccountBalanceLayout.setOnClickListener {
            navigateTo(root, Screen.ACCOUNT)
        }

        selectProposalLayout.setOnClickListener {
            handleSelectProposalPress(root)
        }

        vpnProposalPickerFavoriteLayput.setOnClickListener {
            handleFavoriteProposalPress(root)
        }

        connectionButton.setOnClickListener {
            handleConnectionPress(root)
        }

        sharedViewModel.selectedProposal.observe(viewLifecycleOwner) { updateSelectedProposal(it) }

        sharedViewModel.connectionState.observe(viewLifecycleOwner) {
            updateConnStateLabel(it)
            updateConnButtonState(it)
            updateStatsLayoutVisibility()
        }

        sharedViewModel.statistics.observe(viewLifecycleOwner) { updateStatsLabels(it) }

        sharedViewModel.location.observe(viewLifecycleOwner) { updateLocation(it) }

        walletViewModel.balance.observe(viewLifecycleOwner) { updateBalance(it) }

        onBackPress { emulateHomePress() }

        return root
    }

    private fun updateStatsLayoutVisibility() {
        vpnStatsLayout.visibility = if (sharedViewModel.isConnected()) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    private fun updateBalance(balance: BalanceModel) {
        vpnAccountBalanceLabel.text = balance.balance.displayValue
    }

    private fun updateLocation(location: LocationModel) {
        conStatusIP.text = getString(R.string.vpn_ip, location.ip)
        if (location.countryFlagImage == null) {
            vpnStatusCountry.setImageResource(R.drawable.ic_public_black_24dp)
        } else {
            vpnStatusCountry.setImageBitmap(location.countryFlagImage)
        }
    }

    private fun updateSelectedProposal(proposal: ProposalViewItem) {
        vpnSelectedProposalCountryLabel.text = proposal.countryName
        vpnSelectedProposalCountryIcon.setImageBitmap(proposal.countryFlagImage)
        vpnSelectedProposalProviderLabel.text = proposal.providerID
        vpnSelectedProposalProviderLabel.visibility = View.VISIBLE
        vpnProposalPickerFavoriteImage.setImageResource(proposal.isFavoriteResID)
    }

    private fun updateStatsLabels(stats: StatisticsModel) {
        vpnStatsDurationLabel.text = stats.duration
        vpnStatsBytesReceivedLabel.text = stats.bytesReceived.value
        vpnStatsBytesReceivedUnits.text = stats.bytesReceived.units
        vpnStatsBytesSentLabel.text = stats.bytesSent.value
        vpnStatsBytesSentUnits.text = stats.bytesSent.units

        val tokensSpent = PriceUtils.displayMoney(
                ProposalPaymentMoney(amount = stats.tokensSpent, currency = "MYSTT"),
                DisplayMoneyOptions(fractionDigits = 3, showCurrency = false)
        )
        vpnStatsPaid.text = tokensSpent
    }

    private fun updateConnStateLabel(state: ConnectionState) {
        if (state != ConnectionState.CONNECTED && sharedViewModel.reconnecting) {
            connStatusLabel.text = getString(R.string.conn_state_reconnecting)
            return
        }
        val connStateText = when (state) {
            ConnectionState.NOT_CONNECTED, ConnectionState.UNKNOWN -> getString(R.string.conn_state_not_connected)
            ConnectionState.CONNECTED, ConnectionState.IP_NOT_CHANGED -> getString(R.string.conn_state_connected)
            ConnectionState.ON_HOLD -> getString(R.string.conn_state_on_hold)
            ConnectionState.CONNECTING -> getString(R.string.conn_state_connecting)
            ConnectionState.DISCONNECTING -> getString(R.string.conn_state_disconnecting)
        }

        connStatusLabel.text = connStateText
    }

    private fun handleSelectProposalPress(root: View) {
        navigateToProposals(root)
    }

    private fun handleFavoriteProposalPress(root: View) {
        val selectedProposal = sharedViewModel.selectedProposal.value
        if (selectedProposal == null) {
            navigateToProposals(root)
            return
        }

        vpnProposalPickerFavoriteLayput.isEnabled = false
        proposalsViewModel.toggleFavoriteProposal(selectedProposal.id) { updatedProposal ->
            if (updatedProposal != null) {
                vpnProposalPickerFavoriteImage.setImageResource(updatedProposal.isFavoriteResID)
            }

            vpnProposalPickerFavoriteLayput.isEnabled = true
        }
    }

    private fun navigateToProposals(root: View) {
        if (sharedViewModel.canConnect()) {
            navigateTo(root, Screen.PROPOSALS)
        } else {
            showMessage(root.context, getString(R.string.disconnect_to_select_proposal))
        }
    }

    private fun updateConnButtonState(state: ConnectionState) {
        connectionButton.text = when (state) {
            ConnectionState.NOT_CONNECTED, ConnectionState.UNKNOWN -> getString(R.string.connect_button_connect)
            ConnectionState.CONNECTED, ConnectionState.IP_NOT_CHANGED, ConnectionState.ON_HOLD -> getString(R.string.connect_button_disconnect)
            ConnectionState.CONNECTING -> getString(R.string.connect_button_cancel)
            ConnectionState.DISCONNECTING -> getString(R.string.connect_button_disconnecting)
        }

        connectionButton.isEnabled = state != ConnectionState.DISCONNECTING
    }

    private fun handleConnectionPress(root: View) {
        if (!isAdded) {
            return
        }

        if (!walletViewModel.isIdentityRegistered()) {
            navigateTo(root, Screen.ACCOUNT)
            return
        }

        if (sharedViewModel.canConnect()) {
            connect(root.context, walletViewModel.identity.value!!.address)
            return
        }

        if (sharedViewModel.isConnected()) {
            disconnect(root.context)
            return
        }

        cancel()
    }

    private fun connect(ctx: Context, identityAddress: String) {
        val proposal: ProposalViewItem? = sharedViewModel.selectedProposal.value
        if (proposal == null) {
            showMessage(ctx, getString(R.string.vpn_select_proposal_warning))
            return
        }
        job?.cancel()
        connectionButton.isEnabled = false
        job = CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.i(TAG, "Connecting identity $identityAddress to provider ${proposal.providerID} with service ${proposal.serviceType.type}")
                sharedViewModel.connect(identityAddress, proposal.providerID, proposal.serviceType.type)
            } catch (e: CancellationException) {
                // Do nothing.
            } catch (e: ConnectInvalidProposalException) {
                if (isAdded) {
                    showMessage(ctx, getString(R.string.connect_err_invalid_proposal))
                }
                Log.e(TAG, "Invalid proposal", e)
            } catch (e: ConnectInsufficientBalanceException) {
                if (isAdded) {
                    showMessage(ctx, getString(R.string.connect_err_insufficient_balance))
                }
                Log.e(TAG, "Insufficient balance", e)
            } catch (e: Exception) {
                if (isAdded) {
                    showMessage(ctx, getString(R.string.vpn_failed_to_connect))
                }
                Log.e(TAG, "Failed to connect", e)
            }
        }
    }

    private fun disconnect(ctx: Context) {
        connectionButton.isEnabled = false
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            try {
                sharedViewModel.disconnect()
            } catch (e: CancellationException) {
                // Do nothing.
            } catch (e: Exception) {
                if (isAdded) {
                    showMessage(ctx, getString(R.string.vpn_failed_to_disconnect))
                }
                Log.e(TAG, "Failed to disconnect", e)
            }
        }
    }

    private fun cancel() {
        connectionButton.isEnabled = false
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            try {
                sharedViewModel.disconnect()
            } catch (e: CancellationException) {
                // Do nothing.
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel", e)
            }
        }
    }

    companion object {
        private const val TAG = "ProposalsFragment"
    }
}
