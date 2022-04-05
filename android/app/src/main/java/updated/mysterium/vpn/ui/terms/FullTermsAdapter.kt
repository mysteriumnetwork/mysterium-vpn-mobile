package updated.mysterium.vpn.ui.terms

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemFullTermBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.terms.FullVersionTerm

class FullTermsAdapter(context: Context) :
    ContentListAdapter<FullVersionTerm, FullTermsAdapter.FullTermsViewHolder>() {

    private val markwon = Markwon.create(context)
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
            binding.indexTextView.text = markwon.toMarkdown(terms.index.toString())
            binding.titleTextView.text = markwon.toMarkdown(terms.title)
            binding.contentTextView.text = markwon.toMarkdown(terms.content)
            if (!isAccepted) {
                binding.viewBackground.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}
