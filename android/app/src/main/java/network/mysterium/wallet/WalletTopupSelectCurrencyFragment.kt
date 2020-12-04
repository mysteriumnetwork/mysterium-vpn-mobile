package network.mysterium.wallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.MainApplication
import network.mysterium.payment.Currency
import network.mysterium.ui.showMessage
import network.mysterium.vpn.databinding.FragmentWalletTopupSelectCurrencyBinding
import network.mysterium.vpn.databinding.TopupWalletTopupCurrencyListItemBinding

class WalletTopupSelectCurrencyFragment : Fragment() {

    private lateinit var viewModel: WalletTopupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentWalletTopupSelectCurrencyBinding.inflate(inflater, container, false)
        viewModel = (requireActivity().application as MainApplication).appContainer.walletTopupViewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.activity

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val adapter = CurrencyRecyclerAdapter(viewModel)
        adapter.clickListener = {
            val previous = viewModel.currency.value
            viewModel.togglePaymentCurrency(it)
            previous?.let {
                adapter.notifyItemChanged(adapter.currentList.indexOf(it))
            }
            adapter.notifyItemChanged(adapter.currentList.indexOf(it))
        }

        binding.currencyList.adapter = adapter
        binding.currencyList.layoutManager = LinearLayoutManager(context)
        binding.currencyList.addItemDecoration(DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL))

        viewModel.currencies.observe(viewLifecycleOwner) { currencies ->
            adapter.submitList(currencies)
            adapter.notifyDataSetChanged()
        }

        binding.continueButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    viewModel.createPaymentOrder()
                } catch (e: Exception) {
                    Log.e(TAG, "Could not create a payment order", e)
                    showMessage(requireContext(), "Could not initiate the payment")
                }
            }
        }
        viewModel.order.observe(viewLifecycleOwner) {
            if (it.created) {
                showMessage(requireContext(), "Order created")
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "WalletTopupSelectCurrencyFragment"
    }
}

class CurrencyRecyclerAdapter(private val viewModel: WalletTopupViewModel) : ListAdapter<Currency, CurrencyRecyclerAdapter.ViewHolder>(Companion) {

    lateinit var clickListener: (Currency) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel)
    }

    class ViewHolder(val binding: TopupWalletTopupCurrencyListItemBinding, val clickListener: (Currency) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Currency, viewModel: WalletTopupViewModel) {
            binding.currency = item
            binding.viewModel = viewModel
            binding.selectPaymentCurrencyItem.setOnClickListener {
                clickListener(item)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, clickListener: (Currency) -> Unit): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = TopupWalletTopupCurrencyListItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding, clickListener)
            }
        }
    }

    companion object : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean = oldItem.code == newItem.code
    }
}
