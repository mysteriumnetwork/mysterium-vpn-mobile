package updated.mysterium.vpn.ui.nodes.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityFilterBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.countries.Countries
import updated.mysterium.vpn.model.filter.NodeFilter
import updated.mysterium.vpn.model.filter.NodePrice
import updated.mysterium.vpn.model.filter.NodeQuality
import updated.mysterium.vpn.model.filter.NodeType
import updated.mysterium.vpn.model.manual.connect.PresetFilter
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.network.usecase.FilterUseCase
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.connection.ConnectionActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.search.SearchActivity
import java.util.*

class FilterActivity : BaseActivity() {

    companion object {
        const val FILTER_KEY = "FILTER"
        const val COUNTRY_CODE_KEY = "COUNTRY_CODE"
        private const val TAG = "FilterActivity"
        private const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
    }

    private lateinit var binding: ActivityFilterBinding
    private val nodeListAdapter = FilterAdapter()
    private val viewModel: FilterViewModel by inject()
    private val allNodesViewModel: AllNodesViewModel by inject()
    private val nodeFilter = NodeFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getBundleArguments()
        configure()
        subscribeViewModel()
        bindsActions()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun getBundleArguments() {
        binding.countryName.text = getString(R.string.manual_connect_all_countries)
        val bundle = intent.extras
        if (bundle != null) {
            val countryCode = bundle.getString(COUNTRY_CODE_KEY)
            viewModel.countryCode = countryCode
            Countries.values[countryCode]?.image?.let { flagUrl ->
                Glide.with(this)
                    .load(flagUrl)
                    .circleCrop()
                    .into(binding.countryFlag)
            }
            Countries.values[countryCode]?.name?.let { countryName ->
                binding.countryName.text = countryName
            }
            bundle.getParcelable<PresetFilter>(FILTER_KEY)?.let { presetFilter ->
                viewModel.filter = presetFilter
                filterNodes(presetFilter.filterId, countryCode ?: ALL_COUNTRY_CODE)
                presetFilter.title?.let { filterTitle ->
                    binding.nodesTitle.text = filterTitle
                }
                if (presetFilter.filterId == FilterUseCase.ALL_NODES_FILTER_ID) {
                    binding.filtersLayout.filtersLinear.visibility = View.VISIBLE
                } else {
                    binding.filtersLayout.filtersLinear.visibility = View.GONE
                }
            }
        } else {
            val countryCode = viewModel.countryCode
            Countries.values[countryCode]?.image?.let { flagUrl ->
                Glide.with(this)
                    .load(flagUrl)
                    .circleCrop()
                    .into(binding.countryFlag)
            }
            Countries.values[countryCode]?.name?.let { countryName ->
                binding.countryName.text = countryName
            }
            val filter = viewModel.filter
            filter?.title?.let { filterTitle ->
                binding.nodesTitle.text = filterTitle
            }
            viewModel.cacheProposals?.let { proposals ->
                nodeListAdapter.replaceAll(proposals)
            }
        }
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        initProposalListRecycler()
    }

    private fun subscribeViewModel() {
        viewModel.proposalsList.observe(this, { proposals ->
            proposals?.let {
                nodeListAdapter.replaceAll(it)
            }
        })
    }

    private fun bindsActions() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            if (isTaskRoot) {
                navigateToHomeSelection()
            } else {
                finish()
            }
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            navigateToSearch()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionOrHome()
        }
        binding.filtersLayout.typeCardView.setOnClickListener {
            nodeFilter.onTypeChanged()
            viewModel.filterList(nodeFilter)
            changeNodeTypeView(nodeFilter.typeFilter)
        }
        binding.filtersLayout.priceCardView.setOnClickListener {
            nodeFilter.onPriceChanged()
            viewModel.filterList(nodeFilter)
            changeNodePriceView(nodeFilter.priceFilter)
        }
        binding.filtersLayout.qualityCardView.setOnClickListener {
            nodeFilter.onQualityChanged()
            viewModel.filterList(nodeFilter)
            changeNodeQualityView(nodeFilter.qualityFilter)
        }
    }

    private fun initProposalListRecycler() {
        nodeListAdapter.onNodeClickedListener = {
            navigateToHomeConnection(it)
        }
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FilterActivity)
            adapter = nodeListAdapter
        }
    }

    private fun filterNodes(filterId: Int, countryCode: String) {
        allNodesViewModel.proposals.observe(this, {
            val allCountryNodes = it.find { countryNodes ->
                countryNodes.countryCode == countryCode
            }?.proposalList ?: emptyList()
            mapNodesByFilterAndCountry(filterId, allCountryNodes)
        })
    }

    private fun changeNodeQualityView(nodeQuality: NodeQuality) {
        val qualityDrawable = when (nodeQuality) {
            NodeQuality.LOW -> {
                binding.filtersLayout.qualityFilterTextView.setText(R.string.filter_quality_low)
                ContextCompat.getDrawable(this, R.drawable.filter_quality_low)
            }
            NodeQuality.MEDIUM -> {
                binding.filtersLayout.qualityFilterTextView.setText(R.string.filter_quality_medium)
                ContextCompat.getDrawable(this, R.drawable.filter_quality_medium)
            }
            NodeQuality.HIGH -> {
                binding.filtersLayout.qualityFilterTextView.setText(R.string.filter_quality_high)
                ContextCompat.getDrawable(this, R.drawable.filter_quality_high)
            }
        }
        binding.filtersLayout.qualityFilterImageView.setImageDrawable(qualityDrawable)
    }

    private fun changeNodePriceView(nodePrice: NodePrice) {
        val priceDrawable = when (nodePrice) {
            NodePrice.LOW -> {
                binding.filtersLayout.priceFilterTextView.setText(R.string.filter_price_low)
                ContextCompat.getDrawable(this, R.drawable.filter_price_low)
            }
            NodePrice.MEDIUM -> {
                binding.filtersLayout.priceFilterTextView.setText(R.string.filter_price_medium)
                ContextCompat.getDrawable(this, R.drawable.filter_price_medium)
            }
            NodePrice.HIGH -> {
                binding.filtersLayout.priceFilterTextView.setText(R.string.filter_price_high)
                ContextCompat.getDrawable(this, R.drawable.filter_price_high)
            }
        }
        binding.filtersLayout.priceFilterImageView.setImageDrawable(priceDrawable)
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
        binding.filtersLayout.apply {
            residentialTypeSelectedImageView.visibility = View.VISIBLE
            residentialTypeUnselectedImageView.visibility = View.INVISIBLE
            nonResidentialTypeSelectedImageView.visibility = View.VISIBLE
            nonResidentialTypeUnselectedImageView.visibility = View.INVISIBLE
            typeFilterTextView.setText(R.string.filter_type_all)
        }
    }

    private fun showTypeFilterResidential() {
        binding.filtersLayout.apply {
            residentialTypeSelectedImageView.visibility = View.VISIBLE
            residentialTypeUnselectedImageView.visibility = View.INVISIBLE
            nonResidentialTypeSelectedImageView.visibility = View.INVISIBLE
            nonResidentialTypeUnselectedImageView.visibility = View.VISIBLE
            typeFilterTextView.setText(R.string.filter_type_residential)
        }
    }

    private fun showTypeFilterNonResidential() {
        binding.filtersLayout.apply {
            residentialTypeSelectedImageView.visibility = View.INVISIBLE
            residentialTypeUnselectedImageView.visibility = View.VISIBLE
            nonResidentialTypeSelectedImageView.visibility = View.VISIBLE
            nonResidentialTypeUnselectedImageView.visibility = View.INVISIBLE
            typeFilterTextView.setText(R.string.filter_type_non_residential)
        }
    }

    private fun mapNodesByFilterAndCountry(filterId: Int, proposals: List<Proposal>) {
        viewModel.getProposals(filterId, proposals).observe(this, { result ->
            result.onSuccess {
                nodeListAdapter.replaceAll(it)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
            binding.loader.cancelAnimation()
            binding.loader.visibility = View.INVISIBLE
        }
        )
    }

    private fun navigateToHomeSelection() {
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun navigateToHomeConnection(proposal: Proposal) {
        val intent = Intent(this, ConnectionActivity::class.java).apply {
            putExtra(ConnectionActivity.EXTRA_PROPOSAL_MODEL, proposal)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun navigateToSearch() {
        startActivity(Intent(this, SearchActivity::class.java))
    }
}
