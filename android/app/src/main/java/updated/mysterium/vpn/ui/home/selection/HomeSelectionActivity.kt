package updated.mysterium.vpn.ui.home.selection

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityHomeSelectionBinding
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import updated.mysterium.vpn.analytics.ClientBody
import updated.mysterium.vpn.analytics.MysteriumAnalyticBody
import updated.mysterium.vpn.analytics.MysteriumAnalyticService
import updated.mysterium.vpn.common.data.DeviceUtil
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
    private var isInitialListLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCurrentState()
        configure()
        subscribeViewModel()
        bindsAction()

        val service = MysteriumAnalyticService.retrofit.create(MysteriumAnalyticService::class.java)
        val mysteriumAnalyticBody = MysteriumAnalyticBody(
            eventName = "startup",
            client = ClientBody(
                machineID = DeviceUtil.getDeviceID(contentResolver),
                appVersion = "107109",
                osVersion = "11",
                country = "Ukraine",
                consumerID = null
            )
        )
        val call = service.trackEvent(
            "events",
            mysteriumAnalyticBody
        )
        call?.enqueue(
            object : Callback<Unit?> {

                override fun onResponse(call: Call<Unit?>, response: Response<Unit?>) {
                    Log.i(TAG, "onResponse")
                    Log.i(TAG, response.code().toString())
                }

                override fun onFailure(call: Call<Unit?>, t: Throwable) {
                    Log.i(TAG, "onFailure")
                    Log.i(TAG, t.localizedMessage ?: t.toString())
                }
            }
        )
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    override fun onStop() {
        // Update list again when user will back to this screen
        isInitialListLoaded = false
        super.onStop()
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
        getCurrentIpAddress()
        initFiltersList()
        initCountriesList()
        viewModel.initConnectionListener()
        subscribeToResidentCountry()
    }

    private fun subscribeViewModel() {
        allNodesViewModel.filterLoaded.observe(this) {
            if (!isInitialListLoaded) {
                binding.loader.visibility = View.INVISIBLE
                binding.filterCardView.visibility = View.VISIBLE
                binding.countriesCardView.visibility = View.VISIBLE
                val savedFilterId = viewModel.getPreviousFilterId()
                showFilteredList(savedFilterId)
            }
        }
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
                val selectedCountryCode = allNodesAdapter.selectedItem?.countryCode
                val countryCode = if (selectedCountryCode != ALL_COUNTRY_CODE) {
                    selectedCountryCode?.toLowerCase(Locale.ROOT) ?: ALL_COUNTRY_CODE
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
            navigateToConnection(isBackTransition = false)
        }
    }

    private fun subscribeToResidentCountry() {
        viewModel.getResidentCountry().observe(this, {
            it.onSuccess { country ->
                pushyNotifications.subscribe(country)
            }
        })
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
            navigateToConnection(isBackTransition = true)
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
        filtersAdapter.onNewFilterSelected = {
            val filter = filtersAdapter.selectedItem
            viewModel.saveNewFilterId(filter?.filterId)
            showFilteredList(filter?.filterId ?: 0)
        }
        binding.filtersRecyclerView.apply {
            adapter = filtersAdapter
            layoutManager = object : LinearLayoutManager(this@HomeSelectionActivity) {

                override fun canScrollVertically() = false
            }
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

    private fun showFilteredList(filterId: Int) {
        allNodesViewModel.getFilteredListById(filterId).observe(this) {
            it.onSuccess { countries ->
                val sortedCountries = countries.sortedBy { countryNodes ->
                    countryNodes.countryName
                }
                // remove previous or default selection
                sortedCountries.filter { country ->
                    country.isSelected
                }.forEach { country ->
                    country.changeSelectionState()
                }

                // mark saved country
                val selectedItem = sortedCountries.firstOrNull { country ->
                    country.countryCode == viewModel.getPreviousCountryCode()
                }
                selectedItem?.changeSelectionState()
                val countryIndex = sortedCountries.indexOf(selectedItem)
                (binding.nodesRecyclerView.layoutManager as? LinearLayoutManager)?.apply {
                    if (countryIndex != -1) {
                        // scroll to previous country
                        scrollToPositionWithOffset(countryIndex, 0)
                    } else {
                        // scroll to top
                        sortedCountries.first().changeSelectionState()
                        scrollToPositionWithOffset(0, 0)
                    }
                }
                allNodesAdapter.replaceAll(
                    sortedCountries.sortedBy { countryNodes ->
                        countryNodes.countryName
                    }
                )
            }
        }
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
        allNodesAdapter.onCountrySelected = {
            viewModel.saveNewCountryCode(it)
        }
        binding.nodesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeSelectionActivity)
            adapter = allNodesAdapter
        }
    }

    private fun navigateToConnection(isBackTransition: Boolean) {
        if (connectionState == ConnectionState.CONNECTED) {
            val intent = Intent(this, ConnectionActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val transitionAnimation = if (isBackTransition) {
                ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                ).toBundle()
            } else {
                ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                ).toBundle()
            }
            startActivity(intent, transitionAnimation)
        }
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}
