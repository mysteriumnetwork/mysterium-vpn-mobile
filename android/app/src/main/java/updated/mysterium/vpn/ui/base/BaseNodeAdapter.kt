package updated.mysterium.vpn.ui.base

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.manual.connect.PriceLevel
import updated.mysterium.vpn.model.proposal.parameters.NodeType
import updated.mysterium.vpn.model.proposal.parameters.QualityLevel

abstract class BaseNodeAdapter<T, V : BaseNodeAdapter<T, V>.BaseNodeViewHolder> : ContentListAdapter<T, V>() {

    override fun onBindViewHolder(holder: V, position: Int) {
        if (itemCount == 1) {
            holder.bindSingleItemInList(items[position])
        } else {
            when (position) {
                items.lastIndex -> holder.bindLastItem(items[position])
                0 -> holder.bindFirstItem(items[position])
                else -> holder.bindMiddleItem(items[position])
            }
        }
        holder.bind(items[position])
    }

    abstract inner class BaseNodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bindSingleItemInList(item: T)

        abstract fun bindFirstItem(item: T)

        abstract fun bindMiddleItem(item: T)

        abstract fun bindLastItem(item: T)

        abstract fun bind(item: T)

        protected fun getNodeTypeDrawable(nodeType: NodeType): Drawable? {
            val nodeTypeDrawable = if (nodeType == NodeType.RESIDENTIAL) {
                R.drawable.item_residential
            } else {
                R.drawable.item_non_residential
            }
            return ContextCompat.getDrawable(itemView.context, nodeTypeDrawable)
        }

        protected fun getNodeQualityDrawable(qualityLevel: QualityLevel): Drawable? {
            val nodeQualityDrawable = when (qualityLevel) {
                QualityLevel.LOW -> R.drawable.item_quality_low
                QualityLevel.MEDIUM -> R.drawable.item_quality_medium
                QualityLevel.HIGH -> R.drawable.item_quality_high
                QualityLevel.UNKNOWN -> R.drawable.item_quality_unknowned
            }
            return ContextCompat.getDrawable(itemView.context, nodeQualityDrawable)
        }

        protected fun getNodePriceDrawable(priceLevel: PriceLevel): Drawable? {
            val nodePriceDrawable = when (priceLevel) {
                PriceLevel.LOW -> R.drawable.item_price_low
                PriceLevel.MEDIUM -> R.drawable.item_price_medium
                PriceLevel.HIGH -> R.drawable.item_price_high
                PriceLevel.FREE -> R.drawable.item_price_free
            }
            return ContextCompat.getDrawable(itemView.context, nodePriceDrawable)
        }

        protected fun getSingleItemShape(isAvailable: Boolean = true): Drawable? {
            val backgroundRes = if (isAvailable) {
                R.drawable.shape_rounded_card_view
            } else {
                R.drawable.shape_rounded_card_view_unavailable
            }
            return ResourcesCompat.getDrawable(
                itemView.context.resources,
                backgroundRes,
                null
            )
        }

        protected fun getFirstItemShape(isAvailable: Boolean = true): Drawable? {
            val backgroundRes = if (isAvailable) {
                R.drawable.shape_top_rounded
            } else {
                R.drawable.shape_top_rounded_unavailable
            }
            return ResourcesCompat.getDrawable(
                itemView.context.resources,
                backgroundRes,
                null
            )
        }

        protected fun getMiddleItemShape(isAvailable: Boolean = true): Drawable? {
            val backgroundRes = if (isAvailable) {
                R.drawable.shape_square
            } else {
                R.drawable.shape_square_unavailable
            }
            return ResourcesCompat.getDrawable(
                itemView.context.resources,
                backgroundRes,
                null
            )
        }

        protected fun getLastItemShape(isAvailable: Boolean = true): Drawable? {
            val backgroundRes = if (isAvailable) {
                R.drawable.shape_bottom_rounded
            } else {
                R.drawable.shape_bottom_rounded_unavailable
            }
            return ResourcesCompat.getDrawable(
                itemView.context.resources,
                backgroundRes,
                null
            )
        }
    }
}
