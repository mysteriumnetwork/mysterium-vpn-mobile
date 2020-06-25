package network.mysterium.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import network.mysterium.MainApplication
import network.mysterium.ui.list.BaseItem
import network.mysterium.ui.list.BaseListAdapter
import network.mysterium.ui.list.BaseViewHolder
import network.mysterium.vpn.R

class ProposalsQualityFilterListFragment : Fragment() {

    private lateinit var listAdapter: BaseListAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var proposalsViewModel: ProposalsViewModel
    private lateinit var proposalsQualityFilterIncludeFailed: CheckBox
    private lateinit var resetBtn: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_proposals_quality_filter, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        val appContainer = (activity!!.application as MainApplication).appContainer
        proposalsViewModel = appContainer.proposalsViewModel

        toolbar = root.findViewById(R.id.proposals_quality_filter_toolbar)
        resetBtn = root.findViewById(R.id.proposals_quality_filter_reset_btn)
        proposalsQualityFilterIncludeFailed = root.findViewById(R.id.proposals_quality_filter_include_unreachable)

        toolbar.setNavigationOnClickListener {
            hideKeyboard(root)
            navigateTo(root, Screen.PROPOSALS)
        }

        resetBtn.setOnClickListener {
            proposalsViewModel.applyQualityFilter(ProposalFilterQuality())
            navigateTo(root, Screen.PROPOSALS)
        }

        proposalsQualityFilterIncludeFailed.isChecked = proposalsViewModel.filter.quality.qualityIncludeUnreachable
        proposalsQualityFilterIncludeFailed.setOnClickListener {
            val qualityFilter = proposalsViewModel.filter.quality
            qualityFilter.qualityIncludeUnreachable = !qualityFilter.qualityIncludeUnreachable
            proposalsViewModel.applyQualityFilter(qualityFilter)
            navigateTo(root, Screen.PROPOSALS)
        }

        initList(root)

        onBackPress {
            navigateTo(root, Screen.PROPOSALS)
        }
    }

    private fun initList(root: View) {
        val listItems = proposalsViewModel.proposalsQualities().map { QualityItem(it) }
        listAdapter = BaseListAdapter { clicked ->
            val item = clicked as QualityItem?
            if (item != null) {
                proposalsViewModel.applyQualityFilter(item.quality)
                navigateTo(root, Screen.PROPOSALS)
            }
        }

        val list: RecyclerView = root.findViewById(R.id.proposals_quality_filter_list)
        list.adapter = listAdapter
        list.layoutManager = LinearLayoutManager(context)
        list.addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
        listAdapter.submitList(listItems)
    }
}

data class QualityItem(val quality: ProposalFilterQuality) : BaseItem() {

    override val layoutId = R.layout.proposal_filter_quality_item

    override val uniqueId = quality.level

    override fun bind(holder: BaseViewHolder) {
        super.bind(holder)
        val text: TextView = holder.containerView.findViewById(R.id.proposal_quality_filter_item_text)
        text.text = when(quality.level) {
            QualityLevel.ANY -> "Any"
            QualityLevel.HIGH -> "High"
            QualityLevel.MEDIUM -> "Medium+"
            QualityLevel.LOW -> "Low"
        }
    }
}