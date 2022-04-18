package updated.mysterium.vpn.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemSpinnerDnsBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter

class ResidentCountryAdapter :
    ContentListAdapter<String, ResidentCountryAdapter.ResidentCountryHolder>() {

    var onCountrySelected: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ResidentCountryHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_dns, parent, false)
    )

    override fun onBindViewHolder(holder: ResidentCountryHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ResidentCountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemSpinnerDnsBinding.bind(itemView)

        fun bind(item: String) {
            binding.countryCodeTextView.text = item
            itemView.setOnClickListener {
                onCountrySelected?.invoke(items.indexOf(item))
            }
        }
    }
}

