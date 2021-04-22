package updated.mysterium.vpn.ui.favourites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemSavedNodeBinding
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.ui.manual.connect.BaseNodeAdapter

class FavouritesAdapter : BaseNodeAdapter<Proposal, FavouritesAdapter.SavedNodesViewHolder>() {

    var onDeleteClicked: ((Proposal) -> Unit)? = null
    var onProposalClicked: ((Proposal) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SavedNodesViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_saved_node, parent, false)
    )

    inner class SavedNodesViewHolder(itemView: View) : BaseNodeViewHolder(itemView) {

        val binding = ItemSavedNodeBinding.bind(itemView)

        override fun bindSingleItemInList(item: Proposal) {
            binding.itemContent.background = getSingleItemShape(item.isAvailable)
            binding.lastItemMargin.visibility = View.VISIBLE
            binding.divider.visibility = View.GONE
        }

        override fun bindFirstItem(item: Proposal) {
            binding.itemContent.background = getFirstItemShape(item.isAvailable)
            binding.lastItemMargin.visibility = View.GONE
            binding.divider.visibility = View.VISIBLE
        }

        override fun bindMiddleItem(item: Proposal) {
            binding.itemContent.background = getMiddleItemShape(item.isAvailable)
            binding.lastItemMargin.visibility = View.GONE
            binding.divider.visibility = View.VISIBLE
        }

        override fun bindLastItem(item: Proposal) {
            binding.itemContent.background = getLastItemShape(item.isAvailable)
            binding.lastItemMargin.visibility = View.VISIBLE
            binding.divider.visibility = View.GONE
        }

        override fun bind(item: Proposal) {
            binding.nodeCountryTextView.text = item.countryName
            binding.nodeCodeTextView.text = item.providerID
            binding.nodeTypeImage.setImageDrawable(getNodeTypeDrawable(item.nodeType))
            binding.proposalLayout.setOnClickListener {
                onProposalClicked?.invoke(item)
            }
            binding.deleteImageView.setOnClickListener {
                onDeleteClicked?.invoke(item)
            }
            if (item.isAvailable) {
                binding.qualityImageView.visibility = View.VISIBLE
                binding.priceImageView.visibility = View.VISIBLE
                binding.qualityImageView.setImageDrawable(getNodeQualityDrawable(item.qualityLevel))
                binding.priceImageView.setImageDrawable(getNodePriceDrawable(item.priceLevel))
            } else {
                binding.qualityImageView.visibility = View.INVISIBLE
                binding.priceImageView.visibility = View.INVISIBLE
                binding.root.isClickable = false
            }
        }
    }
}
