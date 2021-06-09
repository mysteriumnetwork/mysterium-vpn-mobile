package updated.mysterium.vpn.ui.wallet.top.up

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.FragmentTopUpBinding
import org.koin.android.ext.android.inject

class TopUpsListFragment : Fragment() {

    private companion object {
        const val TAG = "TopUpsListFragment"
    }

    private lateinit var binding: FragmentTopUpBinding
    private val viewModel: TopUpsListViewModel by inject()
    private val topUpsListAdapter = TopUpsListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configure()
        getTopUps()
    }

    private fun configure() {
        binding.topUpsRecycler.apply {
            layoutManager = LinearLayoutManager(this@TopUpsListFragment.requireContext())
            adapter = topUpsListAdapter
        }
    }

    private fun getTopUps() {
        viewModel.getTopUps().observe(viewLifecycleOwner, { result ->
            result.onSuccess { ordersList ->
                topUpsListAdapter.replaceAll(ordersList)
                Log.i(TAG, ordersList.toString())
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        })
    }
}
