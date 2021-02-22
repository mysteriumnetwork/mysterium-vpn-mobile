package updated.mysterium.vpn.ui.manual.connect.filter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentNodeListBinding
import network.mysterium.vpn.databinding.ToolbarBaseConnectBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.filter.NodePrice
import updated.mysterium.vpn.model.filter.NodeQuality
import updated.mysterium.vpn.model.filter.NodeType
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.model.manual.connect.ProposalModel
import updated.mysterium.vpn.ui.manual.connect.BaseConnectActivity

class FilterActivity : BaseConnectActivity() {

    companion object {

        var countryNodesModel: CountryNodesModel? = null
    }

    private lateinit var binding: FragmentNodeListBinding
    private val nodeListAdapter = FilterAdapter()
    private val viewModel: FilterViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNodeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initProposalListRecycler()
        bindsActions()
        subscribeViewModel()
        getNodesList()
    }

    override fun reUseToolbar(toolbarBinding: ToolbarBaseConnectBinding) {
        changeRightIcon(R.drawable.icon_search)
        if (toolbarBinding.root.parent != null) {
            (toolbarBinding.root.parent as ViewGroup).removeView(toolbarBinding.root)
        }
        binding.manualConnectToolbar.addView(toolbarBinding.root)
    }

    private fun getNodesList() {
        countryNodesModel?.let {
            if (it.countryName.isEmpty()) {
                binding.nodesTitle.text = getString(R.string.manual_connect_all_countries)
                nodeListAdapter.isCountryNamedMode = true
            } else {
                binding.nodesTitle.text = it.countryName
            }
            viewModel.applyInitialFilter(it)
        }
    }

    private fun subscribeViewModel() {
        viewModel.proposalsList.observe(
            this,
            { proposals -> showNodeList(proposals) }
        )
    }

    private fun bindsActions() {
        binding.filters.typeCardView.setOnClickListener {
            viewModel.onNodeTypeClicked().observe(
                this,
                { result -> result.onSuccess { changeNodeTypeView(it) } }
            )
        }
        binding.filters.priceCardView.setOnClickListener {
            viewModel.onNodePriceClicked().observe(
                this,
                { result -> result.onSuccess { changeNodePriceView(it) } }
            )
        }
        binding.filters.qualityCardView.setOnClickListener {
            viewModel.onNodeQualityClicked().observe(
                this,
                { result -> result.onSuccess { changeNodeQualityView(it) } }
            )
        }
    }

    private fun showNodeList(proposalList: List<ProposalModel>) {
        nodeListAdapter.replaceAll(proposalList)
    }

    private fun initProposalListRecycler() {
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FilterActivity)
            adapter = nodeListAdapter
        }
    }

    private fun changeNodeQualityView(nodeQuality: NodeQuality) {
        val qualityDrawable = when (nodeQuality) {
            NodeQuality.LOW -> {
                binding.filters.qualityFilterTextView.setText(R.string.filter_quality_low)
                ContextCompat.getDrawable(this, R.drawable.filter_quality_low)
            }
            NodeQuality.MEDIUM -> {
                binding.filters.qualityFilterTextView.setText(R.string.filter_quality_medium)
                ContextCompat.getDrawable(this, R.drawable.filter_quality_medium)
            }
            NodeQuality.HIGH -> {
                binding.filters.qualityFilterTextView.setText(R.string.filter_quality_high)
                ContextCompat.getDrawable(this, R.drawable.filter_quality_high)
            }
        }
        binding.filters.qualityFilterImageView.setImageDrawable(qualityDrawable)
    }

    private fun changeNodePriceView(nodePrice: NodePrice) {
        val priceDrawable = when (nodePrice) {
            NodePrice.LOW -> {
                binding.filters.priceFilterTextView.setText(R.string.filter_price_low)
                ContextCompat.getDrawable(this, R.drawable.filter_price_low)
            }
            NodePrice.MEDIUM -> {
                binding.filters.priceFilterTextView.setText(R.string.filter_price_medium)
                ContextCompat.getDrawable(this, R.drawable.filter_price_medium)
            }
            NodePrice.HIGH -> {
                binding.filters.priceFilterTextView.setText(R.string.filter_price_high)
                ContextCompat.getDrawable(this, R.drawable.filter_price_high)
            }
        }
        binding.filters.priceFilterImageView.setImageDrawable(priceDrawable)
    }

    private fun changeNodeTypeView(updatedNodeType: NodeType) {
        when (updatedNodeType) {
            NodeType.ALL -> {
                showTypeFilterAll()
            }
            NodeType.RESIDENTIAL -> {
                showTypeFilterResidential()
            }
            NodeType.NON_RESIDENTIAL -> {
                showTypeFilterNonResidential()
            }
        }
    }

    private fun showTypeFilterAll() {
        binding.filters.apply {
            residentialTypeSelectedImageView.visibility = View.VISIBLE
            residentialTypeUnselectedImageView.visibility = View.INVISIBLE
            nonResidentialTypeSelectedImageView.visibility = View.VISIBLE
            nonResidentialTypeUnselectedImageView.visibility = View.INVISIBLE
            typeFilterTextView.setText(R.string.filter_type_all)
        }
    }

    private fun showTypeFilterResidential() {
        binding.filters.apply {
            residentialTypeSelectedImageView.visibility = View.VISIBLE
            residentialTypeUnselectedImageView.visibility = View.INVISIBLE
            nonResidentialTypeSelectedImageView.visibility = View.INVISIBLE
            nonResidentialTypeUnselectedImageView.visibility = View.VISIBLE
            typeFilterTextView.setText(R.string.filter_type_residential)
        }
    }

    private fun showTypeFilterNonResidential() {
        binding.filters.apply {
            residentialTypeSelectedImageView.visibility = View.INVISIBLE
            residentialTypeUnselectedImageView.visibility = View.VISIBLE
            nonResidentialTypeSelectedImageView.visibility = View.VISIBLE
            nonResidentialTypeUnselectedImageView.visibility = View.INVISIBLE
            typeFilterTextView.setText(R.string.filter_type_non_residential)
        }
    }
}
