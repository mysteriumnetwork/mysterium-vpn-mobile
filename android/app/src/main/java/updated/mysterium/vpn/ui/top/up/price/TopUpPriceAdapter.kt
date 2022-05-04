package updated.mysterium.vpn.ui.top.up.price

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemCardElementBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.common.extensions.dpToPx
import updated.mysterium.vpn.model.top.up.TopUpPriceCardItem

class TopUpPriceAdapter :
    ContentListAdapter<TopUpPriceCardItem, TopUpPriceAdapter.TopUpPriceViewHolder>() {

    private companion object {
        const val MARGIN_DP = 24f
    }

    private var selectedCardItem: TopUpPriceCardItem? = null
    var onItemSelected: ((TopUpPriceCardItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TopUpPriceViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_card_element, parent, false)
    )

    override fun onBindViewHolder(holder: TopUpPriceViewHolder, position: Int) {
        if (position == items.lastIndex) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.rightMargin = holder.itemView.context.dpToPx(MARGIN_DP)
            holder.itemView.layoutParams = params
        } else if (position == 0) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.leftMargin = holder.itemView.context.dpToPx(MARGIN_DP)
            holder.itemView.layoutParams = params
        }
        holder.bind(items[position])
    }

    inner class TopUpPriceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemCardElementBinding.bind(itemView)

        fun bind(cardItem: TopUpPriceCardItem) {
            binding.cardItemValue.text = itemView.context.getString(R.string.top_up_currency_equivalent, cardItem.price)
            if (cardItem.isSelected) {
                selectedCardItem = cardItem
                onItemSelected?.invoke(cardItem)
                selectedState()
            } else {
                unselectedState()
            }
            binding.cardItemFrame.setOnClickListener {
                if (cardItem != selectedCardItem) {
                    onItemSelected?.invoke(cardItem)
                    selectedCardItem?.changeState()
                    cardItem.changeState()
                    selectedCardItem = cardItem
                    notifyDataSetChanged()
                }
            }
        }

        private fun selectedState() {
            binding.cardItemValue.setTextColor(Color.WHITE)
            binding.cardItemFrame.background = ContextCompat.getDrawable(
                itemView.context, R.drawable.shape_card_element_selected
            )
            binding.shadow.visibility = View.VISIBLE
        }

        private fun unselectedState() {
            binding.cardItemValue.setTextColor(
                itemView.context.getColor(R.color.onboarding_title_dark_blue)
            )
            binding.cardItemFrame.background = ContextCompat.getDrawable(
                itemView.context, R.drawable.shape_card_element_unselected
            )
            binding.shadow.visibility = View.INVISIBLE
        }
    }
}
