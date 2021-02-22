package updated.mysterium.vpn.common.adapters

import androidx.recyclerview.widget.RecyclerView

abstract class ContentListAdapter<T, V : RecyclerView.ViewHolder> : RecyclerView.Adapter<V>() {

    protected val items = mutableListOf<T>()

    fun getItem(predicate: (T) -> Boolean): T? {
        return items.firstOrNull(predicate)
    }

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

    fun addNew(newItem: T) {
        if (items.none { it == newItem }) {
            items.add(newItem)
            notifyDataSetChanged()
        }
    }

    fun addAll(items: List<T>) {
        val position = this.items.size
        this.items.addAll(items)
        notifyItemRangeInserted(position, items.size)
    }

    fun remove(item: T) {
        val index = items.indexOf(item)
        if (index >= 0) {
            removeAt(index)
        }
    }

    fun remove(predicate: (T) -> Boolean) {
        val index = items.indexOfFirst(predicate)
        if (index >= 0) {
            removeAt(index)
        }
    }

    fun update(item: T, predicate: (T) -> Boolean) {
        val index = items.indexOfFirst(predicate)
        if (index >= 0) {
            items[index] = item
            notifyItemChanged(index)
        }
    }

    fun replaceAll(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun getAll() = items

    private fun removeAt(index: Int) {
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
