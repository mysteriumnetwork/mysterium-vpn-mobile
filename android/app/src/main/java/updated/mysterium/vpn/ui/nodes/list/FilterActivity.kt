package updated.mysterium.vpn.ui.nodes.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityFilterBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.location.Countries
import updated.mysterium.vpn.model.filter.NodeFilter
import updated.mysterium.vpn.model.filter.NodePrice
import updated.mysterium.vpn.model.filter.NodeQuality
import updated.mysterium.vpn.model.filter.NodeType
import updated.mysterium.vpn.network.usecase.FilterUseCase
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.search.SearchActivity

class FilterActivity : BaseActivity() {

    companion object {
        const val FILTER_KEY = "FILTER"
        const val COUNTRY_CODE_KEY = "COUNTRY_CODE"
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
        getUserFilterAndCountry()
        configure()
        subscribeViewModel()
        bindsActions()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun getUserFilterAndCountry() {
        binding.countryName.text = getString(R.string.manual_connect_all_countries)
        val countryCode = viewModel.getPreviousCountryCode()
        if (countryCode == ALL_COUNTRY_CODE) {
            nodeListAdapter.isCountryNamedMode = true
        }
        Countries.values[countryCode]?.image?.let { flagUrl ->
            Glide.with(this)
                .load(flagUrl)
                .circleCrop()
                .into(binding.countryFlag)
        }
        Countries.values[countryCode]?.name?.let { countryName ->
            binding.countryName.text = countryName
        }
        viewModel.getPreviousFilter().observe(this) {
            it.onSuccess { presetFilter ->
                presetFilter?.title?.let { filterTitle ->
                    binding.nodesTitle.text = filterTitle
                }
                val filterId = presetFilter?.filterId ?: FilterUseCase.ALL_NODES_FILTER_ID
                allNodesViewModel.getProposals(filterId, countryCode)
                    .observe(this) { result ->
                        result.onSuccess { proposals ->
                            viewModel.cacheProposals = proposals
                            nodeListAdapter.replaceAll(proposals)
                            binding.loader.cancelAnimation()
                            binding.loader.visibility = View.INVISIBLE
                        }
                    }
            }
        }
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        initProposalListRecycler()
    }

    private fun subscribeViewModel() {
        viewModel.proposalsList.observe(this) { proposals ->
            proposals?.let {
                nodeListAdapter.replaceAll(it)
            }
        }
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
            navigateToConnectionIfConnectedOrHome()
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
            navigateToConnection(it)
        }
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FilterActivity)
            adapter = nodeListAdapter
            FastScrollerBuilder(this).useMd2Style().apply {
                ContextCompat.getDrawable(this@FilterActivity, R.drawable.thumb_drawable_scrolling)
                    ?.let {
                        setThumbDrawable(it)
                    }
                disableScrollbarAutoHide()
                build()
            }
        }
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

    private fun navigateToHomeSelection() {
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun navigateToSearch() {
        startActivity(Intent(this, SearchActivity::class.java))
    }
}
