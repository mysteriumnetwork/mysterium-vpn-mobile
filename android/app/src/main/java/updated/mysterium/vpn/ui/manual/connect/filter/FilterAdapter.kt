package updated.mysterium.vpn.ui.manual.connect.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_node.view.*
import network.mysterium.proposal.NodeType
import network.mysterium.proposal.QualityLevel
import network.mysterium.vpn.R
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

        fun bindSingleItemInList(proposal: ProposalModel) {
            itemView.itemContent.background = ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.shape_rounded_card_view,
                null
            )
            itemView.lastItemMargin.visibility = View.VISIBLE
            itemView.divider.visibility = View.GONE
            bind(proposal)
        }

        fun bindFirstItem(proposal: ProposalModel) {
            itemView.itemContent.background = ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.shape_top_rounded,
                null
            )
            itemView.lastItemMargin.visibility = View.GONE
            itemView.divider.visibility = View.VISIBLE
            bind(proposal)
        }

        fun bindLastItem(proposal: ProposalModel) {
            itemView.itemContent.background = ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.shape_bottom_rounded,
                null
            )
            itemView.lastItemMargin.visibility = View.VISIBLE
            itemView.divider.visibility = View.GONE
            bind(proposal)
        }

        fun bindMiddleItem(proposal: ProposalModel) {
            itemView.itemContent.background = ResourcesCompat.getDrawable(
                itemView.context.resources,
                R.drawable.shape_square,
                null
            )
            itemView.lastItemMargin.visibility = View.GONE
            itemView.divider.visibility = View.VISIBLE
            bind(proposal)
        }

        private fun bind(proposal: ProposalModel) {
            if (isCountryNamedMode) {
                itemView.nodeCountryTextView.visibility = View.VISIBLE
                itemView.nodeCountryTextView.text = proposal.countryName
            } else {
                itemView.nodeCountryTextView.visibility = View.GONE
            }
            itemView.nodeCodeTextView.text = proposal.providerID
            val nodeTypeDrawable = if (proposal.nodeType == NodeType.RESIDENTIAL) {
                R.drawable.item_residential
            } else {
                R.drawable.item_non_residential
            }
            itemView.nodeTypeImage.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, nodeTypeDrawable)
            )
            val nodeQualityDrawable = when (proposal.qualityLevel) {
                QualityLevel.LOW -> R.drawable.item_quality_low
                QualityLevel.MEDIUM -> R.drawable.item_quality_medium
                QualityLevel.HIGH -> R.drawable.item_quality_high
                QualityLevel.UNKNOWN -> R.drawable.item_quality_unknowned
            }
            itemView.qualityImageView.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, nodeQualityDrawable)
            )
            val nodePriceDrawable = when (proposal.priceLevel) {
                PriceLevel.LOW -> R.drawable.item_price_low
                PriceLevel.MEDIUM -> R.drawable.item_price_medium
                PriceLevel.HIGH -> R.drawable.item_price_high
                PriceLevel.FREE -> R.drawable.item_price_free
            }
            itemView.priceImageView.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, nodePriceDrawable)
            )
        }
    }
}
