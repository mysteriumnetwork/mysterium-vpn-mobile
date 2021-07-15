package updated.mysterium.vpn.ui.wallet.spendings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.databinding.FragmentSpendingsBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel

class SpendingsFragment : Fragment() {

    private companion object {
        const val TAG = "SpendingsFragment"
    }

    private lateinit var binding: FragmentSpendingsBinding
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val viewModel: SpendingsViewModel by inject()
    private val spendingsAdapter = SpendingsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpendingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configure()
        getExchangeRate()
    }

    private fun configure() {
        binding.spendingsRecycler.apply {
            layoutManager = LinearLayoutManager(this@SpendingsFragment.requireContext())
            adapter = spendingsAdapter
        }
    }

    private fun getExchangeRate() {
        spendingsAdapter.setExchangeRate(exchangeRateViewModel.usdEquivalent)
        getSpendings()
    }

    private fun getSpendings() {
        viewModel.getSpendings().observe(viewLifecycleOwner, { result ->
            result.onSuccess { spendings ->
                spendingsAdapter.replaceAll(spendings)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        })
    }
}
