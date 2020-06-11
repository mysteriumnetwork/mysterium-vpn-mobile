package network.mysterium.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.ui.list.BaseItem
import network.mysterium.ui.list.BaseListAdapter
import network.mysterium.ui.list.BaseViewHolder
import network.mysterium.vpn.R

class ProposalsCountryFilterList : Fragment() {

    private lateinit var fruitListAdapter: BaseListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proposals_country_filter_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // val root = inflater.inflate(R.layout.fragment_proposals, container, false)

        // Some example data to play with
        val fruits = mutableListOf("Apple", "Banana", "Cherry", "Boysenberry")

        // Organise the fruit and add headers
        val fruitsWithAlphabetHeaders = createAlphabetizedFruit(fruits)

        // OMG this adapter can be used for anything without even touching it!
        fruitListAdapter = BaseListAdapter { fruitClicked ->

            // And removing items when we click them has a cool animation!
            fruits.remove((fruitClicked as? FruitItem?)?.name)
            fruitListAdapter.submitList(createAlphabetizedFruit(fruits))
        }

        val proposals_country_filter_list: RecyclerView = view.findViewById(R.id.proposals_country_filter_list)
        proposals_country_filter_list.adapter = fruitListAdapter

        // Now we have the list organised we can display it in one line!
        fruitListAdapter.submitList(fruitsWithAlphabetHeaders)
    }

    private fun createAlphabetizedFruit(fruits: List<String>): MutableList<BaseItem> {

        // Wrap data in list items
        val fruitItems = fruits.map { FruitItem(it) }.sortedBy { it.name }

        val fruitsWithAlphabetHeaders = mutableListOf<BaseItem>()

        // Loop through the fruit list and add headers where we need them
        var currentHeader: String? = null
        fruitItems.forEach { fruit ->
            fruit.name.firstOrNull()?.toString()?.let {
                if (it != currentHeader) {
                    fruitsWithAlphabetHeaders.add(HeaderItem(it))
                    currentHeader = it
                }
            }
            fruitsWithAlphabetHeaders.add(fruit)
        }
        return fruitsWithAlphabetHeaders
    }
}

data class HeaderItem(val letter: String) : BaseItem() {

    override val layoutId = R.layout.layout_header_item

    override val uniqueId = letter

    override fun bind(holder: BaseViewHolder) {
        holder.containerView.findViewById<>()
        holder.text_header.text = letter
    }
}

data class FruitItem(val name: String) : BaseItem() {

    override val layoutId = R.layout.layout_fruit_item

    override val uniqueId = name

    override fun bind(holder: BaseViewHolder) {
        super.bind(holder)
        holder.text_fruit_name.text = name
    }
}