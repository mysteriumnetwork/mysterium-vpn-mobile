package network.mysterium.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * A base adapter that most RecyclerViews should be able to use
 * The style of this adapter, with [BaseItem] used for the list, is based on the excellent
 * [Groupie](https://github.com/lisawray/groupie) library.
 *
 * @param itemClickCallback An optional callback for clicks on an item
 * */
class BaseListAdapter(private val itemClickCallback: ((BaseItem) -> Unit)?) : ListAdapter<BaseItem, BaseViewHolder>(
        AsyncDifferConfig.Builder<BaseItem>(object : DiffUtil.ItemCallback<BaseItem>() {
            override fun areItemsTheSame(oldItem: BaseItem, newItem: BaseItem): Boolean {
                return oldItem.uniqueId == newItem.uniqueId
            }

            override fun areContentsTheSame(oldItem: BaseItem, newItem: BaseItem): Boolean {
                return oldItem == newItem
            }
        }).build()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        // The layoutId is used as the viewType
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return BaseViewHolder(itemView)
    }

    override fun getItemViewType(position: Int) = getItem(position).layoutId

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        getItem(position).bind(holder)
        getItem(position).itemClickCallback = itemClickCallback
    }
}

/**
 * List items used in [BaseViewHolder]. Implement this with items containing data to display
 * */
abstract class BaseItem {
    abstract val layoutId: Int

    // Used to compare items when diffing so RecyclerView knows how to animate
    abstract val uniqueId: Any

    var itemClickCallback: ((T: BaseItem) -> Unit)? = null

    open fun bind(holder: BaseViewHolder) {
        holder.containerView.setOnClickListener { itemClickCallback?.invoke(this) }
    }
}

class BaseViewHolder(override val containerView: View)
    : RecyclerView.ViewHolder(containerView), LayoutContainer