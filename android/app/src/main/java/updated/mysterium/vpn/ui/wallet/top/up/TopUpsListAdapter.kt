package updated.mysterium.vpn.ui.wallet.top.up

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import updated.mysterium.vpn.model.payment.Order
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemTopUpBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.common.date.DateUtil

class TopUpsListAdapter : ContentListAdapter<Order, TopUpsListAdapter.TopUpsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TopUpsListViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_top_up, parent, false)
    )

    override fun onBindViewHolder(holder: TopUpsListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class TopUpsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemTopUpBinding.bind(itemView)

        fun bind(topUp: Order) {
            binding.mystSpentTextView.text = topUp.receiveMyst.toString()
            binding.paidTextView.text = itemView.context.getString(
                R.string.wallet_top_ups_currency,
                topUp.payAmount,
                topUp.payCurrency
            )
            topUp.createdAt?.let {
                binding.timeTextView.text = DateUtil.getHowLongHoursAgo(topUp.createdAt)
            }
        }
    }
}
