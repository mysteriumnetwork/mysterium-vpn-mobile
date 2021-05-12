package updated.mysterium.vpn.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySearchBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.connection.ConnectionActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.nodes.list.FilterAdapter

class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by inject()
    private val allNodesViewModel: AllNodesViewModel by inject()
    private val nodeListAdapter = FilterAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
        subscribeViewModel()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        initProposalListRecycler()
        initHintText()
        allNodesViewModel.getProposals()
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
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
        binding.editText.addTextChangedListener {
            viewModel.search(it.toString())
        }
    }

    private fun subscribeViewModel() {
        viewModel.searchResult.observe(this, {
            if (binding.loaderAnimation.visibility == View.GONE) {
                if (it.isNotEmpty()) {
                    binding.searchLogo.visibility = View.INVISIBLE
                    binding.searchHint.visibility = View.INVISIBLE
                    nodeListAdapter.replaceAll(it)
                } else {
                    binding.searchLogo.visibility = View.VISIBLE
                    binding.searchHint.visibility = View.VISIBLE
                    nodeListAdapter.clear()
                }
            }
        })
        allNodesViewModel.proposals.observe(this, { allNodes ->
            initialDataLoaded()
            viewModel.setAllNodes(allNodes.first().proposalList)
        })
    }

    private fun initialDataLoaded() {
        binding.loaderAnimation.visibility = View.GONE
        binding.loaderAnimation.cancelAnimation()
        binding.searchLogo.visibility = View.VISIBLE
        binding.searchHint.visibility = View.VISIBLE
        if (binding.editText.text.toString().isNotEmpty()) {
            viewModel.search(binding.editText.text.toString())
        }
    }

    private fun initProposalListRecycler() {
        nodeListAdapter.isCountryNamedMode = true
        nodeListAdapter.onNodeClickedListener = {
            navigateToHome(it)
        }
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = nodeListAdapter
        }
    }

    private fun initHintText() {
        val firstPart = getString(R.string.search_hint_first_part)
        val highlighted = "<font color='#FFFFFF'> " + getString(R.string.search_hint_highlighted) + " </font>"
        val secondPart = getString(R.string.search_hint_second_part)
        binding.searchHint.text = Html.fromHtml(
            firstPart + highlighted + secondPart, Html.FROM_HTML_MODE_LEGACY
        )
    }

    private fun navigateToHome(proposal: Proposal) {
        val intent = Intent(this, ConnectionActivity::class.java)
        intent.putExtra(ConnectionActivity.EXTRA_PROPOSAL_MODEL, proposal)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
