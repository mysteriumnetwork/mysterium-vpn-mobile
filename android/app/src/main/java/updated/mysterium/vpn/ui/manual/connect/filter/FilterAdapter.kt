package updated.mysterium.vpn.ui.manual.connect.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.proposal.NodeType
import network.mysterium.proposal.QualityLevel
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemNodeBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.manual.connect.PriceLevel
import updated.mysterium.vpn.model.manual.connect.ProposalModel

class FilterAdapter : ContentListAdapter<ProposalModel, FilterAdapter.NodeListViewHolder>() {

    var isCountryNamedMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NodeListViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_node, parent, false)
    )

    override fun onBindViewHolder(holder: NodeListViewHolder, position: Int) {
        if (itemCount == 1) {
            holder.bindSingleItemInList(items[position])
        } else {
            when (position) {
                items.lastIndex -> holder.bindLastItem(items[position])
                0 -> holder.bindFirstItem(items[position])
                else -> holder.bindMiddleItem(items[position])
            }
        }
    }

    inner class NodeListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemNodeBinding.bind(itemView)

        fun bindSingleItemInList(proposal: ProposalModel) {
            binding.itemContent.background = ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.shape_rounded_card_view,
                null
            )
            binding.lastItemMargin.visibility = View.VISIBLE
            binding.divider.visibility = View.GONE
            bind(proposal)
        }

        fun bindFirstItem(proposal: ProposalModel) {
            binding.itemContent.background = ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.shape_top_rounded,
                null
            )
            binding.lastItemMargin.visibility = View.GONE
            binding.divider.visibility = View.VISIBLE
            bind(proposal)
        }

        fun bindLastItem(proposal: ProposalModel) {
            binding.itemContent.background = ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.shape_bottom_rounded,
                null
            )
            binding.lastItemMargin.visibility = View.VISIBLE
            binding.divider.visibility = View.GONE
            bind(proposal)
        }

        fun bindMiddleItem(proposal: ProposalModel) {
            binding.itemContent.background = ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.shape_square,
                null
            )
            binding.lastItemMargin.visibility = View.GONE
            binding.divider.visibility = View.VISIBLE
            bind(proposal)
        }

        private fun bind(proposal: ProposalModel) {
            if (isCountryNamedMode) {
                binding.nodeCountryTextView.visibility = View.VISIBLE
                binding.nodeCountryTextView.text = proposal.countryName
            } else {
                binding.nodeCountryTextView.visibility = View.GONE
            }
            binding.nodeCodeTextView.text = proposal.providerID
            val nodeTypeDrawable = if (proposal.nodeType == NodeType.RESIDENTIAL) {
                R.drawable.item_residential
            } else {
                R.drawable.item_non_residential
            }
            binding.nodeTypeImage.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, nodeTypeDrawable)
            )
            val nodeQualityDrawable = when (proposal.qualityLevel) {
                QualityLevel.LOW -> R.drawable.item_quality_low
                QualityLevel.MEDIUM -> R.drawable.item_quality_medium
                QualityLevel.HIGH -> R.drawable.item_quality_high
                QualityLevel.UNKNOWN -> R.drawable.item_quality_unknowned
            }
            binding.qualityImageView.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, nodeQualityDrawable)
            )
            val nodePriceDrawable = when (proposal.priceLevel) {
                PriceLevel.LOW -> R.drawable.item_price_low
                PriceLevel.MEDIUM -> R.drawable.item_price_medium
                PriceLevel.HIGH -> R.drawable.item_price_high
                PriceLevel.FREE -> R.drawable.item_price_free
            }
            binding.priceImageView.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, nodePriceDrawable)
            )
        }
    }
}
