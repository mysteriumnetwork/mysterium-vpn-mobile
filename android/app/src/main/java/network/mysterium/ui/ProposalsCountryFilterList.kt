package network.mysterium.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.ui.list.BaseItem
import network.mysterium.ui.list.BaseListAdapter
import network.mysterium.ui.list.BaseViewHolder
import network.mysterium.vpn.R

class ProposalsCountryFilterList : Fragment() {

    private lateinit var fruitListAdapter: BaseListAdapter
    private lateinit var feedbackToolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_proposals_country_filter_list, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        feedbackToolbar = root.findViewById(R.id.proposals_country_filter_toolbar)
        feedbackToolbar.setNavigationOnClickListener {
            hideKeyboard(root)
            navigateTo(root, Screen.PROPOSALS)
        }

        initList(root)

        onBackPress {
            navigateTo(root, Screen.PROPOSALS)
        }
    }

    private fun initList(root: View) {
        val fruits = mutableListOf("Apple", "Banana", "Cherry", "Boysenberry")
        val fruitsWithAlphabetHeaders = createAlphabetizedFruit(fruits)
        fruitListAdapter = BaseListAdapter { fruitClicked ->
            fruits.remove((fruitClicked as? FruitItem?)?.name)
            fruitListAdapter.submitList(createAlphabetizedFruit(fruits))
        }

        val proposalsCountryFilterList: RecyclerView = root.findViewById(R.id.proposals_country_filter_list)
        proposalsCountryFilterList.adapter = fruitListAdapter
        proposalsCountryFilterList.layoutManager = LinearLayoutManager(context)
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
        Log.i("HEADERITEM", "${letter} bind")
        super.bind(holder)
        val text_header: TextView = holder.containerView.findViewById(R.id.text_header)
        text_header.text = letter
        // holder.text_header.text = letter
    }
}

data class FruitItem(val name: String) : BaseItem() {

    override val layoutId = R.layout.layout_fruit_item

    override val uniqueId = name

    override fun bind(holder: BaseViewHolder) {
        Log.i("FruitItem", "${name} bind")
        super.bind(holder)
        val text_header: TextView = holder.containerView.findViewById(R.id.text_fruit_name)
        text_header.text = name
    }
}