package updated.mysterium.vpn.ui.payment.method

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemPaymentMethodBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.payment.Gateway
import updated.mysterium.vpn.model.payment.GatewayCardItem

class PaymentMethodAdapter :
    ContentListAdapter<Gateway, PaymentMethodAdapter.PaymentMethodViewHolder>() {

    var onItemSelected: ((Gateway) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PaymentMethodViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_payment_method, parent, false)
    )

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemPaymentMethodBinding.bind(itemView)
        fun bind(item: Gateway) {
            GatewayCardItem.from(item)?.let { gatewayCardItem ->
                val backgroundColor =
                    ContextCompat.getDrawable(itemView.context, gatewayCardItem.backgroundId)
                val iconColor = ContextCompat.getColor(itemView.context, gatewayCardItem.iconColorId)
                val textColor = ContextCompat.getColor(itemView.context, gatewayCardItem.textColorId)
                val text = itemView.context.getString(gatewayCardItem.titleId)

                binding.paymentMethodItemCard.background = backgroundColor
                binding.paymentMethodIcon.setBackgroundResource(gatewayCardItem.iconId)
                binding.arrowImageView.setColorFilter(iconColor)
                binding.paymentMethodName.setTextColor(textColor)
                binding.paymentMethodName.text = text

                binding.paymentMethodItemCard.setOnClickListener {
                    onItemSelected?.invoke(item)
                }
            }
        }
    }

}

