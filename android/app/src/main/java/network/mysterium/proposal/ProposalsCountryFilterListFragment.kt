package network.mysterium.proposal

import android.annotation.SuppressLint
import android.content.Context
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
import network.mysterium.navigation.Screen
import network.mysterium.ui.hideKeyboard
import network.mysterium.ui.BaseItem
import network.mysterium.ui.BaseListAdapter
import network.mysterium.ui.BaseViewHolder
import network.mysterium.navigation.navigateTo
import network.mysterium.navigation.onBackPress
import network.mysterium.vpn.R

class ProposalsCountryFilterList : Fragment() {

    private lateinit var listAdapter: BaseListAdapter
    private lateinit var feedbackToolbar: Toolbar
    private lateinit var proposalsViewModel: ProposalsViewModel
    private lateinit var proposalsCountryFilterResetBtn: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_proposals_country_filter, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        val appContainer = (requireActivity().application as MainApplication).appContainer
        proposalsViewModel = appContainer.proposalsViewModel

        feedbackToolbar = root.findViewById(R.id.proposals_country_filter_toolbar)
        proposalsCountryFilterResetBtn = root.findViewById(R.id.proposals_country_filter_reset_btn)

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
        val listItems = proposalsViewModel.proposalsCountries().map {
            val selected = proposalsViewModel.filter.country.code == it.code
            CountryItem(root.context, it, selected)
        }
        listAdapter = BaseListAdapter { clicked ->
            val item = clicked as CountryItem?
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

data class CountryItem(val ctx: Context, val country: ProposalFilterCountry, val selected: Boolean) : BaseItem() {

    override val layoutId = R.layout.proposal_filter_country_item

    override val uniqueId = country.code

    @SuppressLint("SetTextI18n")
    override fun bind(holder: BaseViewHolder) {
        super.bind(holder)
        val countryText: TextView = holder.containerView.findViewById(R.id.proposal_filter_country_text)
        val countryImg: RoundedImageView = holder.containerView.findViewById(R.id.proposal_filter_country_img)
        val proposalsCount: TextView = holder.containerView.findViewById(R.id.proposal_filter_country_proposals_count_text)

        countryText.text = country.name
        countryImg.setImageBitmap(country.flagImage)
        proposalsCount.text = "(${country.proposalsCount})"
        if (selected) {
            countryText.setTextColor(ctx.getColor(R.color.ColorMain))
        }
    }
}
