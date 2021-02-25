package updated.mysterium.vpn.ui.manual.connect.select.node.all

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.FragmentAllNodesBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.ui.manual.connect.filter.FilterActivity
import java.util.Locale

class AllNodesFragment : Fragment() {

    companion object {
        private const val TAG = "CountrySelectFragment"
    }

    private lateinit var binding: FragmentAllNodesBinding
    private val viewModel: AllNodesViewModel by inject()
    private val allNodesAdapter = AllNodesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllNodesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProposalListRecycler()
        bindsAction()
        getProposalList()
    }

    private fun initProposalListRecycler() {
        binding.nodesRecyclerView.apply {
            allNodesAdapter.onCountryClickListener = { navigateToNodeList(it) }
            layoutManager = LinearLayoutManager(requireContext())
            adapter = allNodesAdapter
        }
    }

    private fun bindsAction() {
        binding.sortByView.setOnClickListener {
            viewModel.getSortedProposal().observe(
                viewLifecycleOwner,
                { result ->
                    result.onSuccess { proposalList ->
                        allNodesAdapter.replaceAll(proposalList)
                    }

                    result.onFailure { throwable ->
                        Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                        // TODO("Replace for error dialog or something else")
                    }
                }
            )
            viewModel.changeSortType().observe(
                viewLifecycleOwner,
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
        viewModel.getInitialProposals().observe(
            viewLifecycleOwner,
            {
                it.onSuccess { proposalList ->
                    allNodesAdapter.replaceAll(proposalList)
                }
                it.onFailure { throwable ->
                    Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                    // TODO("Replace for error dialog or something else")
                }
            })
    }

    private fun navigateToNodeList(countryNodesModel: CountryNodesModel) {
        FilterActivity.countryNodesModel = countryNodesModel
        val intent = Intent(requireContext(), FilterActivity::class.java)
        startActivity(intent)
    }
}
