package updated.mysterium.vpn.ui.home.selection

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.PresetFilterItemBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.manual.connect.PresetFilter

class FiltersAdapter : ContentListAdapter<PresetFilter, FiltersAdapter.FiltersHolder>() {

    var selectedItem: PresetFilter? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FiltersHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.preset_filter_item, parent, false)
    )

    override fun onBindViewHolder(holder: FiltersHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class FiltersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = PresetFilterItemBinding.bind(itemView)

        fun bind(item: PresetFilter) {
            binding.filterName.text = item.title
                ?: itemView.context.getString(R.string.home_selection_all_nodes)
            if (item.isSelected) {
                selectedItem = item
                selectedState(item)
            } else {
                unselectedState(item)
            }
            itemView.setOnClickListener {
                if (item != selectedItem) {
                    item.changeSelectionState()
                    selectedItem?.changeSelectionState()
                    selectedItem = item
                    notifyDataSetChanged()
                }
            }
        }

        private fun selectedState(item: PresetFilter) {
            binding.filterName.setTextColor(Color.WHITE)
            binding.root.background = ContextCompat.getDrawable(
                itemView.context, R.drawable.background_country_selected
            )
            Glide.with(itemView.context)
                .load(item.selectedResId)
                .circleCrop()
                .into(binding.filterLogo)
        }

        private fun unselectedState(item: PresetFilter) {
            binding.filterName.setTextColor(
                ContextCompat.getColor(itemView.context, R.color.onboarding_title_dark_blue)
            )
            binding.root.background = null
            Glide.with(itemView.context)
                .load(item.unselectedResId)
                .circleCrop()
                .into(binding.filterLogo)
        }
    }
}
