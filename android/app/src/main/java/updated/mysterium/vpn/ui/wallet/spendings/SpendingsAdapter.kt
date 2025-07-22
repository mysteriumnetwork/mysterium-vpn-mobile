package updated.mysterium.vpn.ui.wallet.spendings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemSpendingBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.common.data.DataUtil
import updated.mysterium.vpn.common.data.UnitFormatter
import updated.mysterium.vpn.common.date.DateUtil
import updated.mysterium.vpn.common.location.Countries
import updated.mysterium.vpn.model.filter.NodeType
import updated.mysterium.vpn.model.session.Spending
import java.util.*

class SpendingsAdapter : ContentListAdapter<Spending, SpendingsAdapter.SpendingViewHolder>() {

    private companion object {
        const val UNKNOWN = "Unknown country"
    }

    private var exchangeRate: Double? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SpendingViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_spending, parent, false)
    )

    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setExchangeRate(exchangeRate: Double) {
        this.exchangeRate = exchangeRate
    }

    inner class SpendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemSpendingBinding.bind(itemView)

        fun bind(spending: Spending) {
            val nodeType = NodeType.from(spending.nodeType)
            val nodeTypeDrawable = if (nodeType == NodeType.RESIDENTIAL) {
                ContextCompat.getDrawable(itemView.context, R.drawable.icon_residential_spending)
            } else {
                ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.icon_non_residential_spending
                )
            }
            val dataReceivedText = "${UnitFormatter.bytesDisplay(spending.dataSize).value} " +
                    UnitFormatter.bytesDisplay(spending.dataSize).units
            val mystSpent = DataUtil.convertTokenToMyst(spending.tokenSpend)
            binding.nodeTypeImageView.setImageDrawable(nodeTypeDrawable)
            binding.countryTextView.text = Countries
                .values[spending.countryName.lowercase(Locale.ROOT)]
                ?.name
                ?: UNKNOWN
            binding.durationTextView.text = DateUtil.convertToDateType(spending.duration * 1000)
            binding.mystSpentTextView.text = String.format("%.3f", mystSpent)
            binding.qualityTextView.text = spending.quality
            binding.sizeTextView.text = dataReceivedText
            binding.connectedTextView.text = itemView.context.getString(
                R.string.wallet_spendings_connected_value,
                DateUtil.dateDiffInDaysFromCurrent(spending.started)
            )
            binding.itemFrame.setOnClickListener {
                onItemClicked()
            }
            exchangeRate?.let {
                binding.usdSpentTextView.text = String.format("%.3f", (mystSpent * it))
            }
        }

        private fun onItemClicked() {
            if (binding.divider.visibility == View.VISIBLE) {
                showShortItem()
            } else {
                showFullItem()
            }
        }

        private fun showFullItem() {
            binding.divider.visibility = View.VISIBLE
            binding.qualityTitle.visibility = View.VISIBLE
            binding.qualityTextView.visibility = View.VISIBLE
            binding.sizeTitle.visibility = View.VISIBLE
            binding.sizeTextView.visibility = View.VISIBLE
            binding.connectedTitle.visibility = View.VISIBLE
            binding.connectedTextView.visibility = View.VISIBLE
            binding.arrowImageView.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, R.drawable.icon_drop_up_png)
            )
        }

        private fun showShortItem() {
            binding.divider.visibility = View.GONE
            binding.qualityTitle.visibility = View.GONE
            binding.qualityTextView.visibility = View.GONE
            binding.sizeTitle.visibility = View.GONE
            binding.sizeTextView.visibility = View.GONE
            binding.connectedTitle.visibility = View.GONE
            binding.connectedTextView.visibility = View.GONE
            binding.arrowImageView.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, R.drawable.icon_drop_down_png)
            )
        }
    }
}
