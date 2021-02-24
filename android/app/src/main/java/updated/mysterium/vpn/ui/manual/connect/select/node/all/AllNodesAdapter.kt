package updated.mysterium.vpn.ui.manual.connect.select.node.all

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemCountryNodesBinding
import updated.mysterium.vpn.model.manual.connect.CountryNodesModel
import updated.mysterium.vpn.ui.manual.connect.BaseNodeAdapter

class AllNodesAdapter : BaseNodeAdapter<CountryNodesModel, AllNodesAdapter.CountrySelectHolder>() {

    companion object {
        private const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
    }

    var onCountryClickListener: ((CountryNodesModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CountrySelectHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_country_nodes, parent, false)
    )

    inner class CountrySelectHolder(itemView: View) : BaseNodeViewHolder(itemView) {

        val binding = ItemCountryNodesBinding.bind(itemView)

        override fun bindSingleItemInList(item: CountryNodesModel) {
            binding.itemContent.background = getSingleItemShape()
            binding.lastItemMargin.visibility = View.VISIBLE
            binding.divider.visibility = View.GONE
        }

        override fun bindFirstItem(item: CountryNodesModel) {
            binding.itemContent.background = getFirstItemShape()
            binding.lastItemMargin.visibility = View.GONE
            binding.divider.visibility = View.VISIBLE
        }

        override fun bindMiddleItem(item: CountryNodesModel) {
            binding.itemContent.background = getMiddleItemShape()
            binding.lastItemMargin.visibility = View.GONE
            binding.divider.visibility = View.VISIBLE
        }

        override fun bindLastItem(item: CountryNodesModel) {
            binding.itemContent.background = getLastItemShape()
            binding.lastItemMargin.visibility = View.VISIBLE
            binding.divider.visibility = View.GONE
        }

        override fun bind(item: CountryNodesModel) {
            if (item.countryCode == ALL_COUNTRY_CODE) {
                binding.countryName.text = itemView.context.getString(R.string.manual_connect_all_countries)
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
            itemView.setOnClickListener { onCountryClickListener?.invoke(item) }
        }
    }
}
