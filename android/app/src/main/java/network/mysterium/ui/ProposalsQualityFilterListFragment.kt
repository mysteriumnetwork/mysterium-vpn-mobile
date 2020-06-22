package network.mysterium.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.makeramen.roundedimageview.RoundedImageView
import network.mysterium.MainApplication
import network.mysterium.ui.list.BaseItem
import network.mysterium.ui.list.BaseListAdapter
import network.mysterium.ui.list.BaseViewHolder
import network.mysterium.vpn.R

class ProposalsQualityFilterListFragment : Fragment() {

    private lateinit var listAdapter: BaseListAdapter
    private lateinit var feedbackToolbar: Toolbar
    private lateinit var proposalsViewModel: ProposalsViewModel
    private lateinit var proposalsCountryFilterResetBtn: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_proposals_quality_filter_list, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        val appContainer = (activity!!.application as MainApplication).appContainer
        proposalsViewModel = appContainer.proposalsViewModel

        feedbackToolbar = root.findViewById(R.id.proposals_country_filter_toolbar)
        proposalsCountryFilterResetBtn = root.findViewById(R.id.proposals_quality_filter_reset_btn)

        feedbackToolbar.setNavigationOnClickListener {
            hideKeyboard(root)
            navigateTo(root, Screen.PROPOSALS)
        }

        proposalsCountryFilterResetBtn.setOnClickListener {
            proposalsViewModel.applyCountryFilter(ProposalFilterCountry("", "", null))
            navigateTo(root, Screen.PROPOSALS)
        }

        initList(root)

        onBackPress {
            navigateTo(root, Screen.PROPOSALS)
        }
    }

    private fun initList(root: View) {
        val listItems = proposalsViewModel.proposalsCountries().map { QualityItem(it) }
        listAdapter = BaseListAdapter { clicked ->
            val item = clicked as QualityItem?
            if (item != null) {
                proposalsViewModel.applyCountryFilter(item.country)
                navigateTo(root, Screen.PROPOSALS)
            }
        }

        val list: RecyclerView = root.findViewById(R.id.proposals_country_filter_list)
        list.adapter = listAdapter
        list.layoutManager = LinearLayoutManager(context)
        list.addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
        listAdapter.submitList(listItems)
    }
}

data class QualityItem(val country: ProposalFilterCountry) : BaseItem() {

    override val layoutId = R.layout.proposal_filter_country_item

    override val uniqueId = country.code

    override fun bind(holder: BaseViewHolder) {
        super.bind(holder)
        val countryText: TextView = holder.containerView.findViewById(R.id.proposal_filter_country_text)
        val countryImg: RoundedImageView = holder.containerView.findViewById(R.id.proposal_filter_country_img)

        countryText.text = country.name
        countryImg.setImageBitmap(country.flagImage)
    }
}