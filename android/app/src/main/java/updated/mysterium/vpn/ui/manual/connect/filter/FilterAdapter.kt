package updated.mysterium.vpn.ui.manual.connect.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemNodeBinding
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.ui.manual.connect.BaseNodeAdapter

class FilterAdapter : BaseNodeAdapter<Proposal, FilterAdapter.NodeListViewHolder>() {

    var isCountryNamedMode = false
    var onNodeClickedListener: ((Proposal) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NodeListViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_node, parent, false)
    )

    inner class NodeListViewHolder(itemView: View) : BaseNodeViewHolder(itemView) {

        val binding = ItemNodeBinding.bind(itemView)

        override fun bindSingleItemInList(item: Proposal) {
            binding.itemContent.background = getSingleItemShape()
            binding.lastItemMargin.visibility = View.VISIBLE
            binding.divider.visibility = View.GONE
        }

        override fun bindFirstItem(item: Proposal) {
            binding.itemContent.background = getFirstItemShape()
            binding.lastItemMargin.visibility = View.GONE
            binding.divider.visibility = View.VISIBLE
        }

        override fun bindMiddleItem(item: Proposal) {
            binding.itemContent.background = getMiddleItemShape()
            binding.lastItemMargin.visibility = View.GONE
            binding.divider.visibility = View.VISIBLE
        }

        override fun bindLastItem(item: Proposal) {
            binding.itemContent.background = getLastItemShape()
            binding.lastItemMargin.visibility = View.VISIBLE
            binding.divider.visibility = View.GONE
        }

        override fun bind(item: Proposal) {
            if (isCountryNamedMode) {
                binding.nodeCountryTextView.visibility = View.VISIBLE
                binding.nodeCountryTextView.text = item.countryName
            } else {
                binding.nodeCountryTextView.visibility = View.GONE
            }
            binding.nodeCodeTextView.text = item.providerID
            binding.nodeTypeImage.setImageDrawable(getNodeTypeDrawable(item.nodeType))
            binding.qualityImageView.setImageDrawable(getNodeQualityDrawable(item.qualityLevel))
            binding.priceImageView.setImageDrawable(getNodePriceDrawable(item.priceLevel))
            binding.itemContent.setOnClickListener {
                onNodeClickedListener?.invoke(item)
            }
        }
    }
}
