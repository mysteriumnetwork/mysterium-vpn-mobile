package updated_mysterium_vpn.ui.manual_connect.county

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.FragmentCountrySelectBinding
import org.koin.android.ext.android.inject
import java.util.Locale

class CountrySelectFragment : Fragment() {

    private lateinit var binding: FragmentCountrySelectBinding
    private val viewModel: CountrySelectViewModel by inject()
    private val countrySelectAdapter = CountrySelectAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountrySelectBinding.inflate(layoutInflater)
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
            layoutManager = LinearLayoutManager(requireContext())
            adapter = countrySelectAdapter
        }
    }

    private fun bindsAction() {
        binding.sortByView.setOnClickListener {
            viewModel.getSortedProposal().observe(
                    viewLifecycleOwner,
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
            viewModel.changeSortType().observe(
                    viewLifecycleOwner,
                    { result ->
                        result.onSuccess {
                            binding.sortByView.text = it.toString().capitalize(Locale.getDefault())
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
                        countrySelectAdapter.replaceAll(proposalList)
                    }
                    it.onFailure { throwable ->
                        Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                        // TODO("Replace for error dialog or something else")
                    }
                })
    }

    companion object {
        private const val TAG = "CountrySelectFragment"
    }
}
