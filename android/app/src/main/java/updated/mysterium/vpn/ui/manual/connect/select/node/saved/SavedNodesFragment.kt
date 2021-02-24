package updated.mysterium.vpn.ui.manual.connect.select.node.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.FragmentSavedNodesBinding
import org.koin.android.ext.android.inject

class SavedNodesFragment : Fragment() {

    private lateinit var binding: FragmentSavedNodesBinding
    private val viewModel: SavedNodesViewModel by inject()
    private val savedNodesAdapter = SavedNodesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedNodesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSavedListRecycler()
    }

    private fun initSavedListRecycler() {
        savedNodesAdapter.onDeleteClicked = {
            viewModel.deleteNodeFromFavourite(it)
        }
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = savedNodesAdapter
        }
        getFavouritesList()
    }

    private fun getFavouritesList() {
        viewModel.getSavedNodes().observe(viewLifecycleOwner, { result ->
            result.onSuccess {
                savedNodesAdapter.replaceAll(it)
            }
            result.onFailure {
                if (it is NoSuchElementException) {
                    deleteListTitles()
                }
            }
        })
    }

    private fun deleteListTitles() {
        binding.nodeTextView.visibility = View.INVISIBLE
        binding.priceTextView.visibility = View.INVISIBLE
        binding.qualityTextView.visibility = View.INVISIBLE
    }
}
