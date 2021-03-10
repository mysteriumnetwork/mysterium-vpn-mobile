package updated.mysterium.vpn.ui.manual.connect.select.node.all

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.FragmentAllNodesBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.CountryNodes
import updated.mysterium.vpn.model.manual.connect.SortType
import updated.mysterium.vpn.ui.manual.connect.filter.FilterActivity

class AllNodesFragment : Fragment() {

    companion object {
        private const val TAG = "CountrySelectFragment"
    }

    private lateinit var binding: FragmentAllNodesBinding
    private val viewModel: AllNodesViewModel by inject()
    private val allNodesAdapter = AllNodesAdapter()
    private var sortType = SortType.NODES

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
        loadInitialProposalList()
    }

    private fun initProposalListRecycler() {
        binding.nodesRecyclerView.apply {
            allNodesAdapter.onCountryClickListener = { navigateToNodeList(it) }
            layoutManager = LinearLayoutManager(requireContext())
            adapter = allNodesAdapter
        }
    }

    private fun bindsAction() {
        binding.nodesTextView.setOnClickListener {
            if (sortType != SortType.NODES) {
                sortType = SortType.NODES
                binding.nodesTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.semi_transparent_arrow_drop_down, 0, 0, 0
                )
                binding.countriesTextView.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0
                )
                getSortedProposal()
            }
        }
        binding.countriesTextView.setOnClickListener {
            if (sortType != SortType.COUNTRIES) {
                sortType = SortType.COUNTRIES
                binding.countriesTextView.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.semi_transparent_arrow_drop_down, 0
                )
                binding.nodesTextView.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0
                )
                getSortedProposal()
            }
        }
    }

    private fun loadInitialProposalList() {
        viewModel.getInitialProposals().observe(viewLifecycleOwner, { result ->
            updateProposalList(result)
        })
    }

    private fun getSortedProposal() {
        viewModel.getSortedProposal(sortType).observe(viewLifecycleOwner, { result ->
            updateProposalList(result)
        })
    }

    private fun updateProposalList(result: Result<List<CountryNodes>>) {
        result.onSuccess { proposalList ->
            allNodesAdapter.replaceAll(proposalList)
        }

        result.onFailure { throwable ->
            Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            // TODO("Replace for error dialog or something else")
        }
    }

    private fun navigateToNodeList(countryNodes: CountryNodes) {
        FilterActivity.countryNodes = countryNodes
        val intent = Intent(requireContext(), FilterActivity::class.java)
        startActivity(intent)
    }
}
