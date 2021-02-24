package updated.mysterium.vpn.ui.manual.connect.select.node

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.ActivitySelectBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.ui.manual.connect.filter.FilterActivity
import java.util.Locale

class SelectNodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectBinding
    private val nodeViewModel: SelectNodeViewModel by inject()
    private val countrySelectAdapter = SelectNodeAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initProposalListRecycler()
        bindsAction()
        getProposalList()
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
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
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
