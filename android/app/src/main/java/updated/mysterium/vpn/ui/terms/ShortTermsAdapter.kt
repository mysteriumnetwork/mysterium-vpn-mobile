package updated.mysterium.vpn.ui.terms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemShortTermBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter

class ShortTermsAdapter : ContentListAdapter<String, ShortTermsAdapter.ShortTermsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ShortTermsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_short_term, parent, false)
    )

    override fun onBindViewHolder(holder: ShortTermsViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class ShortTermsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemShortTermBinding.bind(itemView)

        fun bind(term: String, position: Int) {
            if (position == itemCount - 1) {
                // Remove divider from item
                binding.termTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            binding.termTextView.text = term
        }
    }
}
