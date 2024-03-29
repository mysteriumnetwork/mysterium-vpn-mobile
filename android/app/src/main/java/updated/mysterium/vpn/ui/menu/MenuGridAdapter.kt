package updated.mysterium.vpn.ui.menu

import android.graphics.Color
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
                binding.menuItemView.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.shape_menu_active_item)
                binding.itemTitleTextView.setTextColor(Color.WHITE)
                binding.itemLogoImageView.drawable.setTint(
                    ContextCompat.getColor(itemView.context, R.color.menu_active_item_icon_color)
                )
            } else {
                binding.menuItemView.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.shape_menu_inactive_item)
                binding.itemTitleTextView.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.menu_inactive_item_text_color)
                )
                binding.itemLogoImageView.drawable.setTint(
                    ContextCompat.getColor(itemView.context, R.color.menu_inactive_item_icon_color)
                )
            }
            binding.menuItemView.setOnClickListener {
                item.onItemClickListener?.invoke()
            }
        }
    }
}
