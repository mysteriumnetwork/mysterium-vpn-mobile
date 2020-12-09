package network.mysterium.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.payment.Currency
import network.mysterium.vpn.databinding.RegistrationPaymentCryptoListItemBinding

class RegistrationTopupSelectCurrencyFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        val binding = FragmentRegistrationTopupSelectCurrencyBinding.inflate(inflater, container, false)
//        viewModel = (requireActivity().application as MainApplication).appContainer.registrationViewModel
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = this.activity
//
//        val adapter = CurrencyRecyclerAdapter(viewModel)
//        adapter.clickListener = {
//            val previous = viewModel.payCurrency.value
//            viewModel.togglePaymentCurrency(it)
//            previous?.let {
//                adapter.notifyItemChanged(adapter.currentList.indexOf(it))
//            }
//            adapter.notifyItemChanged(adapter.currentList.indexOf(it))
//        }
//
//        binding.currencyList.adapter = adapter
//        binding.currencyList.layoutManager = LinearLayoutManager(context)
//        binding.currencyList.addItemDecoration(DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL))
//        viewModel.currencies.observe(viewLifecycleOwner) { currencies ->
//            adapter.submitList(currencies)
//            adapter.notifyDataSetChanged()
//        }
//
//        binding.selectPaymentCurrencyContinue.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    viewModel.createPaymentOrder()
//                } catch (e: Exception) {
//                    Log.e(TAG, "Could not create a payment order", e)
//                    showMessage(requireContext(), "Could not initiate the payment")
//                }
//            }
//        }
//
//        return binding.root
        return null
    }

    companion object {
        const val TAG = "RegistrationTopupSelectCurrencyFragment"
    }
}

class CurrencyRecyclerAdapter(private val viewModel: RegistrationViewModel) : ListAdapter<Currency, CurrencyRecyclerAdapter.ViewHolder>(Companion) {

    lateinit var clickListener: (Currency) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel)
    }

    class ViewHolder(val binding: RegistrationPaymentCryptoListItemBinding, val clickListener: (Currency) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Currency, viewModel: RegistrationViewModel) {
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
                val binding = RegistrationPaymentCryptoListItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding, clickListener)
            }
        }
    }

    companion object : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean = oldItem.code == newItem.code
    }
}
