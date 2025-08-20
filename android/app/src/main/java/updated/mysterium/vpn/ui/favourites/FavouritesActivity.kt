package updated.mysterium.vpn.ui.favourites

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.ActivityFavouritesBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.search.SearchActivity

class FavouritesActivity : BaseActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private val viewModel: FavouritesViewModel by inject()
    private val allNodesViewModel: AllNodesViewModel by inject()
    private val favouritesAdapter = FavouritesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
        applyInsets(binding.root)
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        initSavedListRecycler()
    }

    private fun subscribeViewModel() {
        allNodesViewModel.proposals.observe(this) {
            getFavouritesList(it)
        }
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionIfConnectedOrHome()
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun initSavedListRecycler() {
        favouritesAdapter.onProposalClicked = {
            navigateToConnection(it)
        }
        favouritesAdapter.onDeleteClicked = { proposal ->
            viewModel.deleteNodeFromFavourite(proposal).observe(this) { result ->
                result.onSuccess {
                    getFavouritesList()
                }
            }
        }
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavouritesActivity)
            adapter = favouritesAdapter
        }
    }

    private fun getFavouritesList(proposals: List<Proposal>? = null) {
        viewModel.getSavedNodes(proposals).observe(this) { result ->
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
        }
    }

    private fun deleteListTitles() {
        binding.nodeTextView.visibility = View.INVISIBLE
        binding.priceTextView.visibility = View.INVISIBLE
        binding.qualityTextView.visibility = View.INVISIBLE
    }

}
