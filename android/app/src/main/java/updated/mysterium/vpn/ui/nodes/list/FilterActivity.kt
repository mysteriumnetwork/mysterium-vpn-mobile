package updated.mysterium.vpn.ui.nodes.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import network.mysterium.ui.Countries
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityFilterBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.PresetFilter
import updated.mysterium.vpn.model.manual.connect.Proposal
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getBundleArguments()
        configure()
        bindsActions()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun getBundleArguments() {
        binding.countryName.text = getString(R.string.manual_connect_all_countries)
        intent.extras?.let {
            val countryCode = it.getString(COUNTRY_CODE_KEY)
            Countries.values[countryCode]?.image?.let { flagUrl ->
                Glide.with(this)
                    .load(flagUrl)
                    .circleCrop()
                    .into(binding.countryFlag)
            }
            Countries.values[countryCode]?.name?.let { countryName ->
                binding.countryName.text = countryName
            }
            it.getParcelable<PresetFilter>(FILTER_KEY)?.let { filter ->
                filterNodes(filter.filterId, countryCode ?: ALL_COUNTRY_CODE)
                filter.title?.let { filterTitle ->
                    binding.nodesTitle.text = filterTitle
                }
            }
        }
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        initProposalListRecycler()
    }

    private fun bindsActions() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            navigateToSearch()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            val intent = if (connectionState == ConnectionState.CONNECTED) {
                Intent(this, ConnectionActivity::class.java)
            } else {
                Intent(this, HomeSelectionActivity::class.java)
            }
            intent.apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
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

    private fun filterNodes(filterId: Int, countryCode: String) {
        allNodesViewModel.proposals.observe(this, {
            val allCountryNodes = it.find { countryNodes ->
                countryNodes.countryCode == countryCode
            }?.proposalList ?: emptyList()
            mapNodesByFilterAndCountry(filterId, allCountryNodes)
        })
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

    private fun navigateToHome(proposal: Proposal) {
        val intent = Intent(this, ConnectionActivity::class.java)
        intent.putExtra(ConnectionActivity.EXTRA_PROPOSAL_MODEL, proposal)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun navigateToSearch() {
        startActivity(Intent(this, SearchActivity::class.java))
    }
}
