package updated.mysterium.vpn.ui.manual.connect.select.node

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentCountrySelectBinding
import network.mysterium.vpn.databinding.ToolbarBaseConnectBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.ui.manual.connect.BaseConnectActivity
import updated.mysterium.vpn.ui.manual.connect.filter.FilterActivity
import java.util.*


class SelectNodeActivity : BaseConnectActivity() {

    private lateinit var binding: FragmentCountrySelectBinding
    private val nodeViewModel: SelectNodeViewModel by inject()
    private val countrySelectAdapter = SelectNodeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCountrySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initProposalListRecycler()
        bindsAction()
        getProposalList()
    }

    override fun configureToolbar(toolbarBinding: ToolbarBaseConnectBinding) {
        changeRightIcon(R.drawable.icon_search)
        if (toolbarBinding.root.parent != null) {
            (toolbarBinding.root.parent as ViewGroup).removeView(toolbarBinding.root)
        }
        binding.manualConnectToolbar.addView(toolbarBinding.root)
    }

    private fun initProposalListRecycler() {
        binding.nodesRecyclerView.apply {
            countrySelectAdapter.onCountryClickListener = { navigateToNodeList(it) }
            layoutManager = LinearLayoutManager(this@SelectNodeActivity)
            adapter = countrySelectAdapter
        }
    }

    private fun bindsAction() {
        binding.sortByView.setOnClickListener {
            nodeViewModel.getSortedProposal().observe(
                this,
                { result ->
                    result.onSuccess { proposalList ->
                        countrySelectAdapter.replaceAll(proposalList)
                    }

                    result.onFailure { throwable ->
                        Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                        // TODO("Replace for error dialog or something else")
                    }
                }
            )
            nodeViewModel.changeSortType().observe(
                this,
                { result ->
                    result.onSuccess {
                        binding.sortByView.text = it.toString()
                            .toLowerCase(Locale.getDefault())
                            .capitalize(Locale.getDefault())
                    }
                }
            )
        }
    }

    private fun getProposalList() {
        nodeViewModel.getInitialProposals().observe(
            this,
            {
                it.onSuccess { proposalList ->
                    countrySelectAdapter.replaceAll(proposalList)
                }
                it.onFailure { throwable ->
                    Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                    // TODO("Replace for error dialog or something else")
                }
            })
    }

    private fun navigateToNodeList(countryNodesModel: CountryNodesModel) {
        FilterActivity.countryNodesModel = countryNodesModel
        val intent = Intent(this, FilterActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "CountrySelectFragment"
    }
}
