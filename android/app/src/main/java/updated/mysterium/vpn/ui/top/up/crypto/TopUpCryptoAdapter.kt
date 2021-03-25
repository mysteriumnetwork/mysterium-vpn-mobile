package updated.mysterium.vpn.ui.top.up.crypto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemCardElementBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.top.up.CryptoCardItem

class TopUpCryptoAdapter : ContentListAdapter<CryptoCardItem, TopUpCryptoAdapter.TopUpCryptoViewHolder>() {

    private companion object {
        const val MARGIN_DP = 24f
    }

    private var selectedCardItem: CryptoCardItem? = null
    var onItemSelected: ((CryptoCardItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TopUpCryptoViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_card_element, parent, false)
    )

    override fun onBindViewHolder(holder: TopUpCryptoViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    fun getSelectedValue() = selectedCardItem?.value

    inner class TopUpCryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemCardElementBinding.bind(itemView)

        fun bind(cardItem: CryptoCardItem, position: Int) {
            binding.cardItemValue.text = cardItem.value
            if (cardItem.isSelected) {
                selectedCardItem = cardItem
                selectedState()
            } else {
                unselectedState()
            }
            binding.cardItemFrame.setOnClickListener {
                if (cardItem != selectedCardItem) {
                    onItemSelected?.invoke(cardItem)
                    selectedCardItem?.changeState()
                    cardItem.changeState()
                    notifyItemChanged(items.indexOf(selectedCardItem))
                    selectedCardItem = cardItem
                    notifyItemChanged(items.indexOf(selectedCardItem))
                }
            }
            if (position == 0) {
                binding.leftDynamicMargin.visibility = View.VISIBLE
            } else if (position == items.lastIndex) {
                binding.rightDynamicMargin.visibility = View.VISIBLE
            }
        }

        private fun selectedState() {
            binding.cardItemValue.setTextColor(
                itemView.context.getColor(R.color.onboarding_current_screen_white)
            )
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
