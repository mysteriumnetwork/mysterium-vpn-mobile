package updated.mysterium.vpn.ui.terms

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemFullTermBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.terms.FullVersionTerm

class FullTermsAdapter : ContentListAdapter<FullVersionTerm, FullTermsAdapter.FullTermsViewHolder>() {

    var isAccepted = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FullTermsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_full_term, parent, false)
    )

    override fun onBindViewHolder(holder: FullTermsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class FullTermsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemFullTermBinding.bind(itemView)

        fun bind(terms: FullVersionTerm) {
            binding.indexTextView.text = terms.index.toString()
            binding.titleTextView.text = terms.title
            binding.contentTextView.text = terms.content
            if (!isAccepted) {
                 binding.viewBackground.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}
