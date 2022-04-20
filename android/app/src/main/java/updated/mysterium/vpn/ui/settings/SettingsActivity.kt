package updated.mysterium.vpn.ui.settings

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySettingsBinding
import network.mysterium.vpn.databinding.ViewItemNatCompatibilityDescriptionBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.countries.CountriesUtil
import updated.mysterium.vpn.common.extensions.calculateRectOnScreen
import updated.mysterium.vpn.common.extensions.isDarkThemeOn
import updated.mysterium.vpn.common.extensions.onItemSelected
import updated.mysterium.vpn.common.ui.DimenUtils
import updated.mysterium.vpn.common.ui.FlowablePopupWindow
import updated.mysterium.vpn.model.settings.DnsOption
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.menu.SpinnerArrayAdapter
import kotlin.math.abs

class SettingsActivity : BaseActivity() {

    private companion object {
        const val POPUP_WINDOW_TOP_OFFSET_DP = 30
        const val POPUP_WINDOW_END_OFFSET_DP = 30
        const val POPUP_WINDOW_WIDTH_DP = 226
        const val TAG = "SettingsActivity"
        val DNS_OPTIONS = listOf(
            DnsOption(
                translatableValueResId = R.string.settings_dns_cloudflare,
                backendValue = "auto"
            ),
            DnsOption(
                translatableValueResId = R.string.settings_dns_system,
                backendValue = "system"
            ),
            DnsOption(
                translatableValueResId = R.string.settings_dns_provider,
                backendValue = "provider"
            )
        )
    }

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        setUpDnsSpinner()
        setUpResidentCountryList()
        checkPreviousDnsOption()
        checkPreviousResidentCountry()
        checkCurrentLightMode()
        checkNatCompatibility()
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionOrHome()
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.selectedCountryFrame.setOnClickListener {
            changeCountryListVisibility()
        }
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeLightMode(isChecked)
            if (isChecked) {
                applyDarkTheme()
            } else {
                applyLightTheme()
            }
        }
        binding.isNatAvailableCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNatOption(isChecked)
        }
        binding.natHelperFrameButton.setOnClickListener {
            showNatCompatibilityPopUpWindow()
        }
    }

    private fun checkCurrentLightMode() {
        binding.darkModeSwitch.isChecked = isDarkThemeOn()
    }

    private fun checkNatCompatibility() {
        binding.isNatAvailableCheckBox.isChecked = viewModel.isNatCompatibilityAvailable()
    }

    private fun applyDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    private fun applyLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
    }

    private fun setUpDnsSpinner() {
        val languagesList = DNS_OPTIONS.map { getString(it.translatableValueResId) }
        val spinnerAdapter = SpinnerArrayAdapter(
            this,
            R.layout.item_spinner_dns,
            languagesList
        )
        binding.dnsSpinner.apply {
            binding.dnsSpinnerFrame.setOnClickListener {
                performClick()
            }
            adapter = spinnerAdapter
            onItemSelected {
                spinnerAdapter.selectedPosition = it
                viewModel.saveDnsOption(DNS_OPTIONS[it].backendValue)
            }
        }
    }

    private fun checkPreviousDnsOption() {
        viewModel.getSavedDnsOption()?.let { selectedValue ->
            val dnsOption = DNS_OPTIONS.find { it.backendValue == selectedValue }
            binding.dnsSpinner.setSelection(DNS_OPTIONS.indexOf(dnsOption))
        }
    }

    private fun checkPreviousResidentCountry() {
        viewModel.getResidentCountry().observe(this) { result ->
            val countriesList = CountriesUtil.getAllResidentCountries()
            result.onSuccess { residentDigitCode ->
                val residentCountry = countriesList.find { it.code == residentDigitCode }
                binding.selectedCountry.text = residentCountry?.fullName
                    ?: countriesList.first().fullName
            }
            result.onFailure {
                binding.selectedCountry.text = countriesList.first().fullName
                Log.e(TAG, it.localizedMessage ?: it.toString())
            }
        }
    }

    private fun setUpResidentCountryList() {
        val countriesList = CountriesUtil.getAllResidentCountries()
        val countriesListName = countriesList.map { it.fullName }
        binding.selectedCountry.text = countriesListName.first()
        ResidentCountryAdapter().apply {
            addAll(countriesListName)
            onCountrySelected = { position ->
                binding.selectedCountry.text = countriesListName[position]
                viewModel.saveResidentCountry(countriesList[position].code)
                binding.residentCountryList.visibility = View.INVISIBLE
            }
            binding.residentCountryList.adapter = this
        }
    }

    private fun changeCountryListVisibility() {
        if (binding.residentCountryList.visibility == View.INVISIBLE) {
            calculateSpinnerSize()
            binding.residentCountryList.visibility = View.VISIBLE
        } else {
            binding.residentCountryList.visibility = View.INVISIBLE
        }
    }

    private fun calculateSpinnerSize() {
        val size = Point()
        windowManager.defaultDisplay.getSize(size) // get screen size
        val location = IntArray(2)
        binding.residentCountryList.getLocationOnScreen(location) // get view coordinates
        val popUpHeight = size.y - location[1] // distance from view to screen bottom
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            resources.getDimension(R.dimen.margin_padding_size_large),
            resources.displayMetrics
        ).toInt()
        if (popUpHeight > margin) {
            val lp = binding.residentCountryList.layoutParams
            lp.height = popUpHeight - margin
            binding.residentCountryList.layoutParams = lp
        }
    }

    private fun showNatCompatibilityPopUpWindow() {
        val bindingPopUpView = ViewItemNatCompatibilityDescriptionBinding.inflate(
            LayoutInflater.from(this)
        )

        FlowablePopupWindow(
            contentView = bindingPopUpView.root,
            width = DimenUtils.dpToPx(POPUP_WINDOW_WIDTH_DP)
        ).apply {
            gravity = Gravity.TOP
            xOffset = DimenUtils.dpToPx(POPUP_WINDOW_END_OFFSET_DP)
            yOffset = getPopUpWindowVerticalOffset()
        }.show()
    }

    private fun getPopUpWindowVerticalOffset(): Int {
        val baseViewRectangle = binding.root.calculateRectOnScreen()
        val hintButtonViewRectangle = binding.natHelperFrameButton.calculateRectOnScreen()
        val distance = abs(baseViewRectangle.top - hintButtonViewRectangle.bottom).toInt()
        return distance + DimenUtils.dpToPx(POPUP_WINDOW_TOP_OFFSET_DP)
    }
}
