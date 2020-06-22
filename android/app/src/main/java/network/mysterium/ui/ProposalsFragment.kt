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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.makeramen.roundedimageview.RoundedImageView
import network.mysterium.AppContainer
import network.mysterium.MainApplication
import network.mysterium.ui.list.BaseItem
import network.mysterium.ui.list.BaseListAdapter
import network.mysterium.ui.list.BaseViewHolder
import network.mysterium.vpn.R

class ProposalsFragment : Fragment() {

    private lateinit var proposalsViewModel: ProposalsViewModel
    private lateinit var appContainer: AppContainer

    private lateinit var proposalsListRecyclerView: RecyclerView
    private lateinit var proposalsCloseButton: TextView
    private lateinit var proposalsSearchInput: EditText
    private lateinit var proposalsSwipeRefresh: SwipeRefreshLayout
    private lateinit var proposalsProgressBar: ProgressBar
    private lateinit var proposalsFilterCountry: LinearLayout
    private lateinit var proposalsFilterPrice: LinearLayout
    private lateinit var proposalsFilterQuality: LinearLayout
    private lateinit var proposalsFilterNodeType: LinearLayout
    private lateinit var proposalsFilterLayout: ConstraintLayout
    private lateinit var proposalsFilterCountryValue: TextView
    private lateinit var proposalsFilterPriceValue: TextView
    private lateinit var proposalsFilterQualityValue: TextView
    private lateinit var proposalsFilterNodeTypeValue: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_proposals, container, false)

        appContainer = (activity!!.application as MainApplication).appContainer
        proposalsViewModel = appContainer.proposalsViewModel

        // Initialize UI elements.
        proposalsListRecyclerView = root.findViewById(R.id.proposals_list)
        proposalsCloseButton = root.findViewById(R.id.proposals_close_button)
        proposalsSearchInput = root.findViewById(R.id.proposals_search_input)
        proposalsSwipeRefresh = root.findViewById(R.id.proposals_list_swipe_refresh)
        proposalsProgressBar = root.findViewById(R.id.proposals_progress_bar)
        proposalsFilterCountry = root.findViewById(R.id.proposals_filter_country_layoyt)
        proposalsFilterLayout = root.findViewById(R.id.proposals_filters_layout)
        proposalsFilterPrice = root.findViewById(R.id.proposals_filter_price_layout)
        proposalsFilterCountryValue = root.findViewById(R.id.proposals_filter_country_value)
        proposalsFilterPriceValue = root.findViewById(R.id.proposals_filter_price_value)
        proposalsFilterQualityValue = root.findViewById(R.id.proposals_filter_quality_value)
        proposalsFilterNodeTypeValue = root.findViewById(R.id.proposals_filter_node_type_value)
        proposalsFilterQuality = root.findViewById(R.id.proposals_filter_quality_layout)
        proposalsFilterNodeType = root.findViewById(R.id.proposals_filter_node_type_layout)

        proposalsFilterCountry.setOnClickListener {
            navigateTo(root, Screen.PROPOSALS_COUNTRY_FILTER_LIST)
        }

        proposalsFilterPrice.setOnClickListener {
            navigateTo(root, Screen.PROPOSALS_COUNTRY_FILTER_LIST)
        }

        proposalsFilterQuality.setOnClickListener {
            navigateTo(root, Screen.PROPOSALS_QUALITY_FILTER)
        }

        proposalsFilterNodeType.setOnClickListener {
            navigateTo(root, Screen.PROPOSALS_NODE_TYPE_FILTER)
        }

        proposalsFilterLayout.visibility = View.GONE

        initProposalsList(root)
//        initProposalsSortDropdown(root)
//        initProposalsServiceTypeFilter(root)
        initProposalsSearchFilter()

        proposalsCloseButton.setOnClickListener { handleClose(root) }
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

//    private fun initProposalsServiceTypeFilter(root: View) {
//        // Set current active filter.
//        val activeTabButton = when (proposalsViewModel.filter.serviceType) {
//            ServiceTypeFilter.ALL -> proposalsFiltersAllButton
//            ServiceTypeFilter.FAVORITE -> proposalsFiltersFavoriteButton
//        }
//        setFilterTabActiveStyle(root, activeTabButton)
//
//        proposalsFiltersAllButton.setOnClickListener {
//            proposalsViewModel.filterByServiceType(ServiceTypeFilter.ALL)
//            setFilterTabActiveStyle(root, proposalsFiltersAllButton)
//            setFilterTabInactiveStyle(root, proposalsFiltersFavoriteButton)
//        }
//
//        proposalsFiltersFavoriteButton.setOnClickListener {
//            proposalsViewModel.filterByServiceType(ServiceTypeFilter.FAVORITE)
//            setFilterTabActiveStyle(root, proposalsFiltersFavoriteButton)
//            setFilterTabInactiveStyle(root, proposalsFiltersAllButton)
//        }
//    }


    private fun initProposalsList(root: View) {

        val listAdapter = BaseListAdapter{ selectedproposal ->
            val item = selectedproposal as ProposalItem?
            if (item != null) {
                handleSelectedProposal(root, item.uniqueId)
            }
        }
        proposalsListRecyclerView.adapter = listAdapter
        proposalsListRecyclerView.layoutManager = LinearLayoutManager(context)
        proposalsListRecyclerView.addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
        proposalsSwipeRefresh.setOnRefreshListener {
            proposalsViewModel.refreshProposals {
                proposalsSwipeRefresh.isRefreshing = false
            }
        }

        // Subscribe to proposals changes.
        proposalsViewModel.getProposals().observe(this, Observer { newItems ->
            listAdapter.submitList(createProposalItemsWithGroups(newItems))
            listAdapter.notifyDataSetChanged()

            // Hide progress bar once proposals are loaded.
            proposalsListRecyclerView.visibility = View.VISIBLE
            proposalsProgressBar.visibility = View.GONE
            proposalsFilterLayout.visibility = View.VISIBLE
            setSelectedFilterValues()
        })


        proposalsViewModel.initialProposalsLoaded.observe(this, Observer {loaded ->
            if (loaded) {
                return@Observer
            }

            // If initial proposals failed to load during app init try to load them explicitly.
            proposalsListRecyclerView.visibility = View.GONE
            proposalsProgressBar.visibility = View.VISIBLE
            proposalsViewModel.refreshProposals {}
        })
    }

    private fun createProposalItemsWithGroups(groups: List<ProposalGroupViewItem>): MutableList<BaseItem> {
        val itemsWithHeaders = mutableListOf<BaseItem>()
        groups.forEach { group ->
            itemsWithHeaders.add(ProposalHeaderItem(group.title))
            group.children.forEach { proposal ->
                itemsWithHeaders.add(ProposalItem(proposal))
            }
        }
        return itemsWithHeaders
    }

    private fun setSelectedFilterValues() {
        val filter = proposalsViewModel.filter

        proposalsFilterCountryValue.text = if (filter.country.name != "") {
            filter.country.name
        } else {
            getString(R.string.proposals_filter_country_value_all)
        }
    }

    private fun handleClose(root: View) {
        hideKeyboard(root)
        navigateToMainVpnFragment(root)
    }

    private fun handleSelectedProposal(root: View, proposalID: String) {
        hideKeyboard(root)
        proposalsViewModel.selectProposal(proposalID)
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

data class ProposalItem(val item: ProposalViewItem) : BaseItem() {

    override val layoutId = R.layout.proposal_list_item

    override val uniqueId = item.id

    override fun bind(holder: BaseViewHolder) {
        super.bind(holder)

        val countryFlag: RoundedImageView = holder.containerView.findViewById(R.id.proposal_item_country_flag)
        val countryName: TextView = holder.containerView.findViewById(R.id.proposal_item_country_name)
        val providerID: TextView = holder.containerView.findViewById(R.id.proposal_item_provider_id)
        val qualityLevel: ImageView = holder.containerView.findViewById(R.id.proposal_item_quality_level)

        countryFlag.setImageBitmap(item.countryFlagImage)
        countryName.text = item.countryName
        providerID.text = item.providerID
        qualityLevel.setImageResource(item.qualityResID)
    }
}

data class ProposalHeaderItem(val title: String) : BaseItem() {

    override val layoutId = R.layout.proposal_list_header_item

    override val uniqueId = title

    override fun bind(holder: BaseViewHolder) {
        super.bind(holder)
        val headerText: TextView = holder.containerView.findViewById(R.id.proposal_item_header_text)
        headerText.text = title
    }
}

