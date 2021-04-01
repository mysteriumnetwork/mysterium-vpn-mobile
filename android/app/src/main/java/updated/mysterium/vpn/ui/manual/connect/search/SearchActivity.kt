package updated.mysterium.vpn.ui.manual.connect.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.ActivitySearchBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.filter.FilterAdapter
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.wallet.WalletActivity

class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by inject()
    private val balanceViewModel: BalanceViewModel by inject()
    private val nodeListAdapter = FilterAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindsAction()
        initProposalListRecycler()
        subscribeViewModel()
        balanceViewModel.getCurrentBalance()
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.manualConnectToolbar.onBalanceClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
        binding.editText.addTextChangedListener {
            viewModel.search(it.toString())
        }
        balanceViewModel.balanceLiveData.observe(this, {
            binding.manualConnectToolbar.setBalance(it)
        })
    }

    private fun subscribeViewModel() {
        viewModel.searchResult.observe(this, {
            if (it.isNotEmpty()) {
                binding.searchLogo.visibility = View.INVISIBLE
                nodeListAdapter.replaceAll(it)
            } else {
                binding.searchLogo.visibility = View.VISIBLE
                nodeListAdapter.clear()
            }
        })
        viewModel.initialDataLoaded.observe(this, {
            binding.loaderAnimation.visibility = View.GONE
            binding.loaderAnimation.cancelAnimation()
            binding.searchLogo.visibility = View.VISIBLE
        })
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

    private fun navigateToHome(proposal: Proposal) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(HomeActivity.EXTRA_PROPOSAL_MODEL, proposal)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
