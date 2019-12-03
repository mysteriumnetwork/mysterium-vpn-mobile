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

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.AppContainer
import network.mysterium.MainApplication
import network.mysterium.vpn.R

class ProposalsFragment : Fragment() {

    private lateinit var proposalsViewModel: ProposalsViewModel
    private lateinit var appContainer: AppContainer

    private lateinit var proposalsCloseButton: TextView
    private lateinit var proposalsSearchInput: EditText
    private lateinit var proposalsFiltersAllButton: TextView
    private lateinit var proposalsFiltersOpenvpnButton: TextView
    private lateinit var proposalsFiltersWireguardButton: TextView
    private lateinit var proposalsFiltersSort: Spinner
    private lateinit var proposalsSwipeRefresh: SwipeRefreshLayout
    private lateinit var proposalsList: RecyclerView
    private lateinit var proposalsProgressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_proposals, container, false)

        appContainer = (activity!!.application as MainApplication).appContainer
        proposalsViewModel = appContainer.proposalsViewModel

        // Initialize UI elements.
        proposalsCloseButton = root.findViewById(R.id.proposals_close_button)
        proposalsSearchInput = root.findViewById(R.id.proposals_search_input)
        proposalsFiltersAllButton = root.findViewById(R.id.proposals_filters_all_button)
        proposalsFiltersOpenvpnButton = root.findViewById(R.id.proposals_filters_openvpn_button)
        proposalsFiltersWireguardButton = root.findViewById(R.id.proposals_filters_wireguard_button)
        proposalsFiltersSort = root.findViewById(R.id.proposals_filters_sort)
        proposalsSwipeRefresh = root.findViewById(R.id.proposals_list_swipe_refresh)
        proposalsList = root.findViewById(R.id.proposals_list)
        proposalsProgressBar = root.findViewById(R.id.proposals_progress_bar)

        proposalsCloseButton.setOnClickListener { handleClose(root) }

        initProposalsList(root)
        initProposalsSortDropdown(root)
        initProposalsServiceTypeFilter(root)
        initProposalsSearchFilter()

        onBackPress {
            navigateTo(root, Screen.MAIN)
        }

        return root
    }

    private fun navigateToMainVpnFragment(root: View) {
        navigateTo(root, Screen.MAIN)
    }

    private fun initProposalsSearchFilter() {
        if (proposalsViewModel.filter.searchText != "") {
            proposalsSearchInput.setText(proposalsViewModel.filter.searchText)
        }

        proposalsSearchInput.onChange { proposalsViewModel.filterBySearchText(it) }
    }

    private fun initProposalsServiceTypeFilter(root: View) {
        // Set current active filter.
        val activeTabButton = when (proposalsViewModel.filter.serviceType) {
            ServiceTypeFilter.ALL -> proposalsFiltersAllButton
            ServiceTypeFilter.OPENVPN -> proposalsFiltersOpenvpnButton
            ServiceTypeFilter.WIREGUARD -> proposalsFiltersWireguardButton
        }
        setFilterTabActiveStyle(root, activeTabButton)

        proposalsFiltersAllButton.setOnClickListener {
            proposalsViewModel.filterByServiceType(ServiceTypeFilter.ALL)
            setFilterTabActiveStyle(root, proposalsFiltersAllButton)
            setFilterTabInactiveStyle(root, proposalsFiltersOpenvpnButton)
            setFilterTabInactiveStyle(root, proposalsFiltersWireguardButton)
        }

        proposalsFiltersOpenvpnButton.setOnClickListener {
            proposalsViewModel.filterByServiceType(ServiceTypeFilter.OPENVPN)
            setFilterTabActiveStyle(root, proposalsFiltersOpenvpnButton)
            setFilterTabInactiveStyle(root, proposalsFiltersAllButton)
            setFilterTabInactiveStyle(root, proposalsFiltersWireguardButton)
        }

        proposalsFiltersWireguardButton.setOnClickListener {
            proposalsViewModel.filterByServiceType(ServiceTypeFilter.WIREGUARD)
            setFilterTabActiveStyle(root, proposalsFiltersWireguardButton)
            setFilterTabInactiveStyle(root, proposalsFiltersAllButton)
            setFilterTabInactiveStyle(root, proposalsFiltersOpenvpnButton)
        }
    }

    private fun initProposalsSortDropdown(root: View) {
        ArrayAdapter.createFromResource(root.context, R.array.proposals_sort_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            proposalsFiltersSort.adapter = adapter
            proposalsFiltersSort.onItemSelected { item -> proposalsViewModel.sortBy(item) }
        }
    }

    private fun initProposalsList(root: View) {
        proposalsList.layoutManager = LinearLayoutManager(root.context)
        val items = ArrayList<ProposalViewItem>()
        val proposalsListAdapter = ProposalsListAdapter(items) { handleSelectedProposal(root, it) }
        proposalsList.adapter = proposalsListAdapter
        proposalsList.addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))

        proposalsSwipeRefresh.setOnRefreshListener {
            proposalsViewModel.refreshProposals {
                proposalsSwipeRefresh.isRefreshing = false
            }
        }

        // Subscribe to proposals changes.
        proposalsViewModel.getProposals().observe(this, Observer { newItems ->
            items.clear()
            items.addAll(newItems)
            proposalsListAdapter.notifyDataSetChanged()

            // Hide progress bar once proposals are loaded.
            proposalsList.visibility = View.VISIBLE
            proposalsProgressBar.visibility = View.GONE
        })

        // Subscribe to proposals counters.
        proposalsViewModel.getProposalsCounts().observe(this, Observer { counts ->
            proposalsFiltersAllButton.text = "All (${counts.all})"
            proposalsFiltersOpenvpnButton.text = "Openvpn (${counts.openvpn})"
            proposalsFiltersWireguardButton.text = "Wireguard (${counts.wireguard})"
        })

        proposalsViewModel.initialProposalsLoaded.observe(this, Observer {loaded ->
            if (loaded) {
                return@Observer
            }

            // If initial proposals failed to load during app init try to load them explicitly.
            proposalsList.visibility = View.GONE
            proposalsProgressBar.visibility = View.VISIBLE
            proposalsViewModel.refreshProposals {}
        })
    }

    private fun handleClose(root: View) {
        hideKeyboard(root)
        navigateToMainVpnFragment(root)
    }

    private fun handleSelectedProposal(root: View, proposal: ProposalViewItem) {
        hideKeyboard(root)
        proposalsViewModel.selectProposal(proposal)
        navigateToMainVpnFragment(root)
    }

    private fun setFilterTabActiveStyle(root: View, btn: TextView) {
        btn.setBackgroundColor(ContextCompat.getColor(root.context, R.color.ColorMain))
        btn.setTextColor(ContextCompat.getColor(root.context, R.color.ColorWhite))
    }

    private fun setFilterTabInactiveStyle(root: View, btn: TextView) {
        btn.setBackgroundColor(Color.TRANSPARENT)
        btn.setTextColor(ContextCompat.getColor(root.context, R.color.ColorMain))
    }
}

class ProposalsListAdapter(private var list: List<ProposalViewItem>, private var onItemClickListener: (ProposalViewItem) -> Unit)
    : RecyclerView.Adapter<ProposalsListAdapter.ProposalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProposalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProposalViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ProposalViewHolder, position: Int) {
        val item: ProposalViewItem = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickListener(item)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ProposalViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.proposal_list_item, parent, false)) {

        private var countryFlag: RoundedImageView? = null
        private var countryName: TextView? = null
        private var providerID: TextView? = null
        private var serviceType: ImageView? = null
        private var qualityLevel: ImageView? = null
        private var favorite: ImageView? = null

        init {
            countryFlag = itemView.findViewById(R.id.proposal_item_country_flag)
            countryName = itemView.findViewById(R.id.proposal_item_country_name)
            providerID = itemView.findViewById(R.id.proposal_item_provider_id)
            serviceType = itemView.findViewById(R.id.proposal_item_service_type)
            qualityLevel = itemView.findViewById(R.id.proposal_item_quality_level)
            favorite = itemView.findViewById(R.id.proposal_item_favorite)
        }

        fun bind(item: ProposalViewItem) {
            countryFlag?.setImageBitmap(item.countryFlagImage)
            countryName?.text = item.countryName
            providerID?.text = item.providerID
            serviceType?.setImageResource(item.serviceTypeResID)
            qualityLevel?.setImageResource(item.qualityResID)
            favorite?.setImageResource(item.isFavoriteResID)
        }
    }
}
