package updated.mysterium.vpn.ui.home.selection

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityHomeSelectionBinding
import network.mysterium.vpn.databinding.PopUpDownloadNewApplicationBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.manual.connect.PresetFilter
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.favourites.FavouritesActivity
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.nodes.list.FilterActivity
import java.util.Locale


class HomeSelectionActivity : BaseActivity() {

    companion object {
        const val SHOW_PAYMENT_PROCESSING_BANNER_KEY = "SHOW_PAYMENT_PROCESSING_BANNER"
        private const val ALL_COUNTRY_CODE = "ALL_COUNTRY"
    }

    private lateinit var binding: ActivityHomeSelectionBinding
    private lateinit var bindingNewAppPopUp: PopUpDownloadNewApplicationBinding
    private var dialogPasswordPopup: AlertDialog? = null


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
        applyInsets(binding.root)
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
        viewModel.getCurrentState().observe(this) { result ->
            result.onSuccess { state ->
                handleConnectionState(state)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        }
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        getCurrentIpAddress()
        initFiltersList()
        initCountriesList()
        viewModel.initConnectionListener()
        intent.extras?.get(SHOW_PAYMENT_PROCESSING_BANNER_KEY)?.let {
            showPaymentProcessingBanner()
        }
    }

    private fun subscribeViewModel() {
        allNodesViewModel.initialDataLoaded.observe(this) {
            if (!isInitialListLoaded) {
                binding.loader.visibility = View.INVISIBLE
                binding.filterCardView.visibility = View.VISIBLE
                binding.countriesCardView.visibility = View.VISIBLE
                val savedFilterId = viewModel.getPreviousFilterId()
                showFilteredList(savedFilterId)
            }
        }
        viewModel.connectionState.observe(this) {
            handleConnectionState(it)
        }
        viewModel.showNewAppUrl.observe(this) { link ->
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }
        viewModel.isNewAppPopUpShow.observe(this) { isShow ->
            if (isShow) {
                showNewAppPopup()
            } else {
                dialogPasswordPopup?.dismiss()
                dialogPasswordPopup = null
            }
        }
        viewModel.isNewAppNotificationShow.observe(this) { isShow ->
            binding.notificationNewApp.isVisible = isShow
            binding.headerView.isInvisible = isShow
        }
    }

    private fun bindsAction() {
        binding.newAppNotification.setOnClickListener {
            viewModel.openNewAppLink(NewAppPopupSource.NOTIFICATION, getString(R.string.new_pop_up_url_download_url))
        }
        binding.newAppNotificationClose.setOnClickListener {
            viewModel.closeNewAppPopups(NewAppPopupSource.NOTIFICATION)
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            startActivity(Intent(this, MenuActivity::class.java))
        }
        binding.manualConnectToolbar.onRightButtonClicked {
            startActivity(Intent(this, FavouritesActivity::class.java))
        }
        binding.smartConnectButton.setOnClickListener {
            navigateToConnection(isConnectIntent = true)
        }
        binding.manualNodeSelectionButton.setOnClickListener {
            navigateToFilter()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnection(isBackTransition = false)
        }
        binding.paymentProcessingLayout.closeBannerButton.setOnClickListener {
            binding.paymentProcessingLayout.root.visibility = View.GONE
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
            navigateToConnection(isBackTransition = true)
        }
    }

    private fun getCurrentIpAddress() {
        viewModel.getLocation().observe(this) {
            it.onSuccess { location ->
                binding.ipTextView.text = location.ip
            }
        }
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
        getSystemPresets()
    }

    private fun getSystemPresets() {
        viewModel.getSystemPresets().observe(this) {
            it.onSuccess { filters ->
                filtersAdapter.replaceAll(filters)
                applySavedFilter(viewModel.getPreviousFilterId(), filters)
            }
            it.onFailure { throwable ->
                wifiNetworkErrorPopUp {
                    getSystemPresets()
                }
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        }
    }

    private fun showFilteredList(filterId: Int) {
        allNodesViewModel.getCountryInfoList(filterId).observe(this) {
            it.onSuccess { countryInfoList ->
                val sortedCountryInfoList = countryInfoList.sortedBy { countryInfo ->
                    countryInfo.countryName
                }
                // remove previous or default selection
                sortedCountryInfoList.filter { countryInfo ->
                    countryInfo.isSelected
                }.forEach { countryInfo ->
                    countryInfo.changeSelectionState()
                }
                // mark saved country
                val selectedItem = sortedCountryInfoList.firstOrNull { countryInfo ->
                    countryInfo.countryCode == viewModel.getPreviousCountryCode()
                }
                selectedItem?.changeSelectionState()
                val countryIndex = sortedCountryInfoList.indexOf(selectedItem)
                (binding.nodesRecyclerView.layoutManager as? LinearLayoutManager)?.apply {
                    if (countryIndex != -1) {
                        // scroll to previous country
                        scrollToPositionWithOffset(countryIndex, 0)
                    } else {
                        // scroll to top
                        viewModel.saveNewCountryCode(ALL_COUNTRY_CODE)
                        sortedCountryInfoList.first().changeSelectionState()
                        scrollToPositionWithOffset(0, 0)
                    }
                }
                allNodesAdapter.replaceAll(
                    sortedCountryInfoList.sortedBy { countryInfo ->
                        countryInfo.countryName
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

    private fun showPaymentProcessingBanner() {
        binding.paymentProcessingLayout.root.doOnLayout { view ->
            val leftButton = binding.manualConnectToolbar.findViewById<View>(R.id.leftButton)
            val animationY = leftButton.y + leftButton.height + view.height
            view.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(view, "translationY", animationY)
                .apply {
                    duration = 2000
                    start()
                }
        }
    }

    private fun showNewAppPopup() {
        bindingNewAppPopUp = PopUpDownloadNewApplicationBinding.inflate(layoutInflater)
        dialogPasswordPopup = createPopUp(bindingNewAppPopUp.root, true)
        dialogPasswordPopup?.setOnDismissListener {
            viewModel.closeNewAppPopups(NewAppPopupSource.POP_UP)
        }
        with(bindingNewAppPopUp) {
            acceptButton.setOnClickListener {
                dialogPasswordPopup?.dismiss()
                viewModel.openNewAppLink(
                    NewAppPopupSource.POP_UP,
                    getString(R.string.new_pop_up_url_download_url)
                )
            }
            declineButton.setOnClickListener {
                dialogPasswordPopup?.dismiss()
                viewModel.openNewAppLink(
                    NewAppPopupSource.POP_UP,
                    getString(R.string.new_pop_up_learn_more)
                )
            }
        }
        dialogPasswordPopup?.show()
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, MenuActivity::class.java))
    }

    private fun navigateToFilter() {
        val intent = Intent(this, FilterActivity::class.java).apply {
            val selectedCountryCode = allNodesAdapter.selectedItem?.countryCode
            val countryCode = if (selectedCountryCode != ALL_COUNTRY_CODE) {
                selectedCountryCode?.lowercase(Locale.ROOT) ?: ALL_COUNTRY_CODE
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
}
