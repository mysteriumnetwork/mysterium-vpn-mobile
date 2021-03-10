package updated.mysterium.vpn.ui.manual.connect.filter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityFilterBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.filter.NodeFilter
import updated.mysterium.vpn.model.filter.NodePrice
import updated.mysterium.vpn.model.filter.NodeQuality
import updated.mysterium.vpn.model.filter.NodeType
import updated.mysterium.vpn.model.manual.connect.CountryNodes
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.manual.connect.search.SearchActivity
import updated.mysterium.vpn.ui.wallet.WalletActivity

class FilterActivity : AppCompatActivity() {

    companion object {
        var countryNodes: CountryNodes? = null
    }

    private lateinit var binding: ActivityFilterBinding
    private val nodeListAdapter = FilterAdapter()
    private val viewModel: FilterViewModel by inject()
    private val balanceViewModel: BalanceViewModel by inject()
    private val nodeFilter = NodeFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initProposalListRecycler()
        bindsActions()
        subscribeViewModel()
        getNodesList()
        balanceViewModel.getCurrentBalance()
    }

    private fun getNodesList() {
        countryNodes?.let {
            if (it.countryName.isEmpty()) {
                binding.nodesTitle.text = getString(R.string.manual_connect_all_countries)
                nodeListAdapter.isCountryNamedMode = true
            } else {
                binding.nodesTitle.text = it.countryName
            }
            viewModel.applyInitialFilter(it, nodeFilter)
        }
    }

    private fun subscribeViewModel() {
        viewModel.proposalsList.observe(
            this,
            { proposals -> showNodeList(proposals) }
        )
        balanceViewModel.balanceLiveData.observe(this, {
            binding.manualConnectToolbar.setBalance(it)
        })
    }

    private fun bindsActions() {
        binding.filters.typeCardView.setOnClickListener {
            nodeFilter.onTypeChanged()
            viewModel.filterList(nodeFilter)
            changeNodeTypeView(nodeFilter.typeFilter)
        }
        binding.filters.priceCardView.setOnClickListener {
            nodeFilter.onPriceChanged()
            viewModel.filterList(nodeFilter)
            changeNodePriceView(nodeFilter.priceFilter)
        }
        binding.filters.qualityCardView.setOnClickListener {
            nodeFilter.onQualityChanged()
            viewModel.filterList(nodeFilter)
            changeNodeQualityView(nodeFilter.qualityFilter)
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            navigateToSearch()
        }
        binding.manualConnectToolbar.onBalanceClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
    }

    private fun showNodeList(proposalList: List<Proposal>) {
        nodeListAdapter.replaceAll(proposalList)
    }

    private fun initProposalListRecycler() {
        nodeListAdapter.onNodeClickedListener = {
            navigateToHome(it)
        }
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FilterActivity)
            adapter = nodeListAdapter
        }
    }

    private fun navigateToHome(proposal: Proposal) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(HomeActivity.EXTRA_PROPOSAL_MODEL, proposal)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
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

    private fun navigateToSearch() {
        startActivity(Intent(this, SearchActivity::class.java))
    }
}
