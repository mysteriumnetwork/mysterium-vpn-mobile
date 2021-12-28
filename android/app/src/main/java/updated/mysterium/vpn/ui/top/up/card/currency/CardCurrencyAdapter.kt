package updated.mysterium.vpn.ui.top.up.card.currency

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemCardCurrencyBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.top.up.CurrencyCardItem

class CardCurrencyAdapter :
    ContentListAdapter<CurrencyCardItem, CardCurrencyAdapter.CardCurrencyViewHolder>() {

    private var selectedCardItem: CurrencyCardItem? = null
    var onItemSelected: ((CurrencyCardItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardCurrencyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_card_currency, parent, false)
    )

    override fun onBindViewHolder(holder: CardCurrencyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getSelectedValue() = selectedCardItem?.currency

    inner class CardCurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemCardCurrencyBinding.bind(itemView)

        fun bind(cardItem: CurrencyCardItem) {
            cardItem.currency?.currency?.let { currency ->
                binding.root.context.getString(
                    R.string.card_payment_currency,
                    cardItem.currency.symbol,
                    currency
                )
            }
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
        }

        private fun selectedState() {
            binding.cardItemValue.setTextColor(Color.WHITE)
            binding.cardItemFrame.background = ContextCompat.getDrawable(
                itemView.context, R.drawable.shape_card_currency_selected
            )
        }

        private fun unselectedState() {
            binding.cardItemValue.setTextColor(
                itemView.context.getColor(R.color.onboarding_title_dark_blue)
            )
            binding.cardItemFrame.background = ContextCompat.getDrawable(
                itemView.context, R.drawable.shape_card_currency_unselected
            )
        }
    }
}
