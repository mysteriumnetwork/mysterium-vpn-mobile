package updated.mysterium.vpn.ui.home.selection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityHomeSelectionBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.PresetFilter
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.connection.ConnectionActivity
import updated.mysterium.vpn.ui.favourites.FavouritesActivity
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.nodes.list.FilterActivity
import java.util.*

class HomeSelectionActivity : BaseActivity() {

    private companion object {
        const val TAG = "HomeSelectionActivity"
        const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
    }

    private lateinit var binding: ActivityHomeSelectionBinding
    private val viewModel: HomeSelectionViewModel by inject()
    private val allNodesViewModel: AllNodesViewModel by inject()
    private val allNodesAdapter = AllNodesAdapter()
    private val filtersAdapter = FiltersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCurrentState()
        configure()
        subscribeViewModel()
        bindsAction()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun checkCurrentState() {
        viewModel.getCurrentState().observe(this, { result ->
            result.onSuccess { state ->
                handleConnectionState(state)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        })
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        allNodesViewModel.getProposals()
        getCurrentIpAddress()
        initFiltersList()
        initCountriesList()
        viewModel.initConnectionListener()
    }

    private fun subscribeViewModel() {
        allNodesViewModel.proposals.observe(this, { countries ->
            binding.loader.visibility = View.INVISIBLE
            binding.filterCardView.visibility = View.VISIBLE
            binding.countriesCardView.visibility = View.VISIBLE
            val selectedItem = countries.firstOrNull { country ->
                country.isSelected
            }
            selectedItem?.changeSelectionState()
            val savedCountry = countries?.firstOrNull { country ->
                country.countryCode == viewModel.getPreviousCountryCode()
            }
            savedCountry?.changeSelectionState()
            allNodesAdapter.replaceAll(countries)
            val countryIndex = allNodesAdapter.getAll().indexOf(savedCountry)
            (binding.nodesRecyclerView.layoutManager as? LinearLayoutManager)?.apply {
                scrollToPositionWithOffset(countryIndex, 0)
            }
        })
        viewModel.connectionState.observe(this, {
            handleConnectionState(it)
        })
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            startActivity(Intent(this, MenuActivity::class.java))
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            startActivity(Intent(this, FavouritesActivity::class.java))
        }
        binding.selectNodeButton.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java).apply {
                val countryCode = if (allNodesAdapter.selectedItem?.countryCode != ALL_COUNTRY_CODE) {
                    allNodesAdapter.selectedItem?.countryCode?.toLowerCase(Locale.ROOT)
                        ?: ALL_COUNTRY_CODE
                } else {
                    ALL_COUNTRY_CODE
                }
                putExtra(FilterActivity.COUNTRY_CODE_KEY, countryCode)
                val filter = filtersAdapter.selectedItem
                putExtra(FilterActivity.FILTER_KEY, filter)
                viewModel.saveNewCountryCode(countryCode)
                viewModel.saveNewFilterId(filter?.filterId)
            }
            startActivity(intent)
        }
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnection()
        }
    }

    private fun handleConnectionState(connection: ConnectionState) {
        when (connection) {
            ConnectionState.NOTCONNECTED -> {
                binding.titleTextView.text = getString(R.string.manual_connect_disconnected)
                leftMenu()
            }
            ConnectionState.CONNECTING -> {
                binding.titleTextView.text = getString(R.string.manual_connect_connecting)
                leftBack()
            }
            ConnectionState.CONNECTED -> {
                binding.titleTextView.text = getString(R.string.manual_connect_connected)
                leftBack()
            }
            ConnectionState.ON_HOLD -> {
                binding.titleTextView.text = getString(R.string.manual_connect_on_hold)
                leftBack()
            }
            else -> {
                binding.titleTextView.text = connection.state
                leftMenu()
            }
        }
    }

    private fun leftMenu() {
        binding.manualConnectToolbar.setLeftIcon(
            ContextCompat.getDrawable(this, R.drawable.icon_menu)
        )
        binding.manualConnectToolbar.onLeftButtonClicked {
            navigateToMenu()
        }
    }

    private fun leftBack() {
        binding.manualConnectToolbar.setLeftIcon(
            ContextCompat.getDrawable(this, R.drawable.icon_back)
        )
        binding.manualConnectToolbar.onLeftButtonClicked {
            navigateToConnection()
        }
    }

    private fun getCurrentIpAddress() {
        viewModel.getLocation().observe(this, {
            it.onSuccess { location ->
                binding.ipTextView.text = location.ip
            }
        })
    }

    private fun initFiltersList() {
        binding.filtersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeSelectionActivity)
            adapter = filtersAdapter
        }
        viewModel.getSystemPresets().observe(this, {
            it.onSuccess { filters ->
                filtersAdapter.replaceAll(filters)
                applySavedFilter(viewModel.getPreviousFilterId(), filters)
            }
            it.onFailure { throwable ->
                wifiNetworkErrorPopUp()
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        })
    }

    private fun applySavedFilter(filterId: Int, filters: List<PresetFilter>) {
        var selectedItemIndex = 0
        filters.forEach { presetFilter ->
            if (presetFilter.isSelected) {
                presetFilter.changeSelectionState()
            }
            if (presetFilter.filterId == filterId) {
                selectedItemIndex = filtersAdapter.getAll().indexOf(presetFilter)
                presetFilter.changeSelectionState()
            }
        }
        (binding.filtersRecyclerView.layoutManager as? LinearLayoutManager)?.apply {
            scrollToPositionWithOffset(selectedItemIndex, 0)
        }
    }

    private fun initCountriesList() {
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeSelectionActivity)
            adapter = allNodesAdapter
        }
    }

    private fun navigateToConnection() {
        if (connectionState == ConnectionState.CONNECTED) {
            val intent = Intent(this, ConnectionActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}