package updated.mysterium.vpn.common.adapters

import androidx.recyclerview.widget.RecyclerView

abstract class ContentListAdapter<T, V : RecyclerView.ViewHolder> : RecyclerView.Adapter<V>() {

    protected val items = mutableListOf<T>()

    fun contains(predicate: (T) -> Boolean): Boolean {
        return items.indexOfFirst(predicate) >= 0
    }

    fun add(item: T) {
        items.add(item)
        notifyDataSetChanged()
    }

    fun add(item: T, index: Int) {
        if (index > items.size) {
            return
        }
        items.add(index, item)
        notifyDataSetChanged()
    }

    fun addAll(items: List<T>) {
        val position = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(position, items.size)
    }

    fun replaceAll(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun getAll() = items

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
