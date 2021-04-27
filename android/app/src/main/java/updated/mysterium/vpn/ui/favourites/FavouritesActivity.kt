package updated.mysterium.vpn.ui.favourites

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.ActivityFavouritesBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.manual.connect.search.SearchActivity
import updated.mysterium.vpn.ui.manual.connect.select.node.all.AllNodesViewModel

class FavouritesActivity : BaseActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private val viewModel: FavouritesViewModel by inject()
    private val allNodesViewModel: AllNodesViewModel by inject()
    private val favouritesAdapter = FavouritesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSavedListRecycler()
        subscribeViewModel()
        bindsAction()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun subscribeViewModel() {
        allNodesViewModel.proposals.observe(this, {
            getFavouritesList(it.first().proposalList)
        })
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun initSavedListRecycler() {
        favouritesAdapter.onProposalClicked = {
            navigateToHome(it)
        }
        favouritesAdapter.onDeleteClicked = { proposal ->
            viewModel.deleteNodeFromFavourite(proposal).observe(this, { result ->
                result.onSuccess {
                    getFavouritesList()
                }
            })
        }
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavouritesActivity)
            adapter = favouritesAdapter
        }
        allNodesViewModel.getProposals()
    }

    private fun getFavouritesList(proposals: List<Proposal>? = null) {
        viewModel.getSavedNodes(proposals).observe(this, { result ->
            result.onSuccess {
                favouritesAdapter.replaceAll(it)
            }
            result.onFailure {
                if (it is NoSuchElementException) {
                    deleteListTitles()
                    favouritesAdapter.clear()
                }
            }
            binding.loaderAnimation.visibility = View.GONE
            binding.loaderAnimation.cancelAnimation()
        })
    }

    private fun deleteListTitles() {
        binding.nodeTextView.visibility = View.INVISIBLE
        binding.priceTextView.visibility = View.INVISIBLE
        binding.qualityTextView.visibility = View.INVISIBLE
    }

    private fun navigateToHome(proposal: Proposal) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(HomeActivity.EXTRA_PROPOSAL_MODEL, proposal)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
