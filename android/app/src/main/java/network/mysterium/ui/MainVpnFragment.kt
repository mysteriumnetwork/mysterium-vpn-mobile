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

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import network.mysterium.MainApplication
import network.mysterium.vpn.R

class MainVpnFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var proposalsViewModel: ProposalsViewModel

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
    private lateinit var vpnAccountBalanceLabel: TextView
    private lateinit var vpn_account_balance_layout: ConstraintLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val appContainer = (activity!!.application as MainApplication).appContainer
        sharedViewModel = appContainer.sharedViewModel
        proposalsViewModel = appContainer.proposalsViewModel

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
        vpnAccountBalanceLabel = root.findViewById(R.id.vpn_account_balance_label)
        vpn_account_balance_layout = root.findViewById(R.id.vpn_account_balance_layout)

        feedbackButton.setOnClickListener {
            val drawer = appContainer.drawerLayout
            drawer.openDrawer(GravityCompat.START)
        }

        vpn_account_balance_layout.setOnClickListener {
            navigateTo(root, Screen.ACCOUNT)
        }

        selectProposalLayout.setOnClickListener {
            handleSelectProposalPress(root)
        }

        vpnProposalPickerFavoriteLayput.setOnClickListener {
            handleFavoriteProposalPress(root)
        }

        connectionButton.setOnClickListener {
            handleConnectionPress(root.context)
        }

        sharedViewModel.selectedProposal.observe(this, Observer { updateSelectedProposal(it) })

        sharedViewModel.connectionState.observe(this, Observer {
            updateConnStateLabel(it)
            updateConnButtonState(it)
        })

        sharedViewModel.statistics.observe(this, Observer { updateStatsLabels(it) })

        sharedViewModel.location.observe(this, Observer { updateLocation(it) })

        onBackPress { emulateHomePress() }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    private fun updateLocation(it: LocationViewItem) {
        conStatusIP.text = "IP: ${it.ip}"
        if (it.countryFlagImage == null) {
            vpnStatusCountry.setImageResource(R.drawable.ic_public_black_24dp)
        } else {
            vpnStatusCountry.setImageBitmap(it.countryFlagImage)
        }
    }

    private fun updateSelectedProposal(it: ProposalViewItem) {
        vpnSelectedProposalCountryLabel.text = it.countryName
        vpnSelectedProposalCountryIcon.setImageBitmap(it.countryFlagImage)
        vpnSelectedProposalProviderLabel.text = it.providerID
        vpnSelectedProposalProviderLabel.visibility = View.VISIBLE
        vpnProposalPickerFavoriteImage.setImageResource(it.isFavoriteResID)
    }

    private fun updateStatsLabels(it: StatisticsViewItem) {
        vpnStatsDurationLabel.text = it.duration
        vpnStatsBytesReceivedLabel.text = it.bytesReceived.value
        vpnStatsBytesReceivedUnits.text = it.bytesReceived.units
        vpnStatsBytesSentLabel.text = it.bytesSent.value
        vpnStatsBytesSentUnits.text = it.bytesSent.units
    }

    private fun updateConnStateLabel(it: ConnectionState) {
        val connStateText = when (it) {
            ConnectionState.NOT_CONNECTED, ConnectionState.UNKNOWN -> getString(R.string.conn_state_not_connected)
            ConnectionState.CONNECTED -> getString(R.string.conn_state_connected)
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

    private fun updateConnButtonState(it: ConnectionState) {
        connectionButton.text = when (it) {
            ConnectionState.NOT_CONNECTED, ConnectionState.UNKNOWN -> getString(R.string.connect_button_connect)
            ConnectionState.CONNECTED -> getString(R.string.connect_button_disconnect)
            ConnectionState.CONNECTING -> getString(R.string.connect_button_cancel)
            ConnectionState.DISCONNECTING -> getString(R.string.connect_button_disconnecting)
        }

        connectionButton.isEnabled = when (it) {
            ConnectionState.DISCONNECTING -> false
            else -> true
        }
    }

    private fun handleConnectionPress(ctx: Context) {
        if (sharedViewModel.canConnect()) {
            connect(ctx)
            return
        }

        if (sharedViewModel.canDisconnect()) {
            disconnect(ctx)
            return
        }

        cancel()
    }

    private fun connect(ctx: Context) {
        val proposal: ProposalViewItem? = sharedViewModel.selectedProposal.value
        if (proposal == null) {
            showMessage(ctx, getString(R.string.vpn_select_proposal_warning))
            return
        }
        job?.cancel()
        connectionButton.isEnabled = false
        job = CoroutineScope(Dispatchers.Main).launch {
            try {
                sharedViewModel.connect(proposal.providerID, proposal.serviceType.type)
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Do nothing.
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
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel", e)
            }
        }
    }

    companion object {
        private const val TAG = "ProposalsFragment"
    }
}
