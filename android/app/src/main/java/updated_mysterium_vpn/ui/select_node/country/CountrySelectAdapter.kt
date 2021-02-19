package updated_mysterium_vpn.ui.select_node.country

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_country_nodes.view.*
import network.mysterium.vpn.R
import updated_mysterium_vpn.common.adapters.ContentListAdapter
import updated_mysterium_vpn.model.manual_connect.CountryNodesModel

class CountrySelectAdapter : ContentListAdapter<CountryNodesModel, CountrySelectAdapter.CountrySelectHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CountrySelectHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_country_nodes, parent, false)
    )

    override fun onBindViewHolder(holder: CountrySelectHolder, position: Int) {
        when (position) {
            items.lastIndex -> holder.bindLastItem(items[position])
            0 -> holder.bindFirstItem(items[position])
            else -> holder.bindMiddleItem(items[position])
        }
    }

    inner class CountrySelectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindFirstItem(proposal: CountryNodesModel) {
            itemView.itemContent.background = ResourcesCompat.getDrawable(
                    itemView.context.resources,
                    R.drawable.shape_top_rounded,
                    null
            )
            itemView.lastItemMargin.visibility = View.GONE
            itemView.divider.visibility = View.VISIBLE
            bind(proposal)
        }

        fun bindLastItem(proposal: CountryNodesModel) {
            itemView.itemContent.background = ResourcesCompat.getDrawable(
                    itemView.context.resources,
                    R.drawable.shape_bottom_rounded,
                    null
            )
            itemView.lastItemMargin.visibility = View.VISIBLE
            itemView.divider.visibility = View.GONE
            bind(proposal)
        }

        fun bindMiddleItem(proposal: CountryNodesModel) {
            itemView.itemContent.background = ResourcesCompat.getDrawable(
                    itemView.context.resources,
                    R.drawable.shape_square,
                    null
            )
            itemView.lastItemMargin.visibility = View.GONE
            itemView.divider.visibility = View.VISIBLE
            bind(proposal)
        }

        private fun bind(proposal: CountryNodesModel) {
            if (proposal.countryCode == ALL_COUNTRY_CODE) {
                itemView.countryName.text = itemView.context.getString(R.string.manual_connect_all_countries)
                Glide.with(itemView.context)
                        .load(proposal.countryFlagRes)
                        .circleCrop()
                        .into(itemView.countryImage)
            } else {
                itemView.countryName.text = proposal.countryName
                Glide.with(itemView.context)
                        .load(proposal.proposalList.first().countryFlagImage)
                        .circleCrop()
                        .into(itemView.countryImage)
            }
            itemView.countryNodesCount.text = proposal.proposalList.size.toString()
        }
    }

    companion object {
        private const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
    }
}
