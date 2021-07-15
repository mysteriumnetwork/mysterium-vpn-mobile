package updated.mysterium.vpn.ui.home.selection

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemCountryNodesBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.manual.connect.CountryNodes

class AllNodesAdapter : ContentListAdapter<CountryNodes, AllNodesAdapter.CountrySelectHolder>() {

    companion object {
        private const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
    }

    var onCountrySelected: ((String) -> Unit)? = null
    var selectedItem: CountryNodes? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CountrySelectHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_country_nodes, parent, false)
    )

    override fun onBindViewHolder(holder: CountrySelectHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CountrySelectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemCountryNodesBinding.bind(itemView)

        fun bind(item: CountryNodes) {
            if (item.countryCode == ALL_COUNTRY_CODE) {
                binding.countryName.text = itemView.context.getString(
                    R.string.manual_connect_all_countries
                )
                Glide.with(itemView.context)
                    .load(item.countryFlagRes)
                    .circleCrop()
                    .into(binding.countryImage)
            } else {
                binding.countryName.text = item.countryName
                Glide.with(itemView.context)
                    .load(item.proposalList.first().countryFlagImage)
                    .circleCrop()
                    .into(binding.countryImage)
            }
            binding.countryNodesCount.text = item.proposalList.size.toString()
            if (item.isSelected) {
                selectedItem = item
                selectedState()
            } else {
                unselectedState()
            }
            itemView.setOnClickListener {
                if (item != selectedItem) {
                    onCountrySelected?.invoke(item.countryCode)
                    item.changeSelectionState()
                    selectedItem?.changeSelectionState()
                    selectedItem = item
                    notifyDataSetChanged()
                }
            }
        }

        private fun selectedState() {
            binding.countryName.setTextColor(Color.WHITE)
            binding.countryNodesCount.setTextColor(Color.WHITE)
            binding.root.background = ContextCompat.getDrawable(
                itemView.context, R.drawable.background_country_selected
            )
        }

        private fun unselectedState() {
            binding.countryName.setTextColor(
                itemView.context.getColor(R.color.onboarding_title_dark_blue)
            )
            binding.countryNodesCount.setTextColor(
                itemView.context.getColor(R.color.onboarding_title_dark_blue)
            )
            binding.root.background = null
        }
    }
}
