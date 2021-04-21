package updated.mysterium.vpn.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemMenuBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.model.menu.MenuItem

class MenuGridAdapter : ContentListAdapter<MenuItem, MenuGridAdapter.MenuGridViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuGridViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
    )

    override fun onBindViewHolder(holder: MenuGridViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MenuGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: MenuItem) {
            val binding = ItemMenuBinding.bind(itemView)
            binding.itemTitleTextView.text = itemView.context.getString(item.titleResId)
            binding.itemLogoImageView.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, item.iconResId)
            )
            when {
                item.dynamicSubtitle != null -> {
                    item.dynamicSubtitle?.let {
                        binding.itemSubTitleTextView.text = it
                    }
                }
                item.subTitleResId != null -> {
                    binding.itemSubTitleTextView.text = itemView.context.getString(item.subTitleResId)
                }
                else -> {
                    binding.itemSubTitleTextView.text = ""
                }
            }
            if (item.isActive) {
                binding.itemTitleTextView.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.onboarding_current_screen_white)
                )
            } else {
                binding.itemTitleTextView.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.manual_connect_icon_white)
                )
            }
            binding.menuItemView.setOnClickListener {
                item.onItemClickListener?.invoke()
            }
        }
    }
}
