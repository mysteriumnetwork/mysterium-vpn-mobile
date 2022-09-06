package updated.mysterium.vpn.ui.payment.method

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemPaymentMethodBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.payment.PaymentOption
import updated.mysterium.vpn.model.payment.PaymentOptionCardItem

class PaymentMethodAdapter :
    ContentListAdapter<PaymentOption, PaymentMethodAdapter.PaymentMethodViewHolder>() {

    var onItemSelected: ((PaymentOption) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PaymentMethodViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_payment_method, parent, false)
    )

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemPaymentMethodBinding.bind(itemView)
        fun bind(item: PaymentOption) {
            PaymentOptionCardItem.from(item)?.let { paymentOption ->
                val text = itemView.context.getString(paymentOption.titleId)
                binding.paymentMethodIcon.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, paymentOption.iconId)
                )
                binding.paymentMethodName.text = text
                binding.paymentMethodItemCard.setOnClickListener {
                    onItemSelected?.invoke(item)
                }
            }
        }
    }

}

