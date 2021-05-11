package updated.mysterium.vpn.ui.settings

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.ListPopupWindow
import network.mysterium.ui.onItemSelected
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySettingsBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.countries.CountriesUtil
import updated.mysterium.vpn.common.extensions.isDarkThemeOn
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.settings.DnsOption
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.connection.ConnectionActivity
import updated.mysterium.vpn.ui.custom.view.LongListPopUpWindow
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.menu.SpinnerArrayAdapter

class SettingsActivity : BaseActivity() {

    private companion object {
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
    private lateinit var listPopupWindow: ListPopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        calculateSpinnerSize()
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
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onConnectClickListener {
            val intent = if (connectionState == ConnectionState.CONNECTED) {
                Intent(this, ConnectionActivity::class.java)
            } else {
                Intent(this, HomeSelectionActivity::class.java)
            }
            intent.apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.residentSpinnerFrame.setOnClickListener {
            listPopupWindow.show()
        }
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeLightMode(isChecked)
            if (isChecked) {
                applyDarkTheme()
            } else {
                applyLightTheme()
            }
        }
    }

    private fun checkCurrentLightMode() {
        binding.darkModeSwitch.isChecked = isDarkThemeOn()
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
        viewModel.getResidentCountry().observe(this, { result ->
            val countriesList = CountriesUtil().getAllCountries()
            result.onSuccess { residentDigitCode ->
                val residentCountry = countriesList.find { it.digitCode == residentDigitCode }
                binding.selectedCountry.text = residentCountry?.fullName
                    ?: countriesList.first().fullName
            }
            result.onFailure {
                binding.selectedCountry.text = countriesList.first().fullName
                Log.e(TAG, it.localizedMessage ?: it.toString())
            }
        })
    }

    private fun setUpResidentCountryList() {
        val countriesList = CountriesUtil().getAllCountries()
        val countriesListName = countriesList.map { it.fullName }
        binding.selectedCountry.text = countriesListName.first()
        val spinnerAdapter = SpinnerArrayAdapter(
            this,
            R.layout.item_spinner_dns,
            countriesListName
        )
        listPopupWindow = LongListPopUpWindow(this).apply {
            inflateView(spinnerAdapter, binding.residentSpinnerFrame)
            setOnItemClickListener { _, _, position, _ ->
                dismiss()
                binding.selectedCountry.text = countriesListName[position]
                viewModel.saveResidentCountry(countriesList[position].digitCode)
            }
        }
    }

    private fun calculateSpinnerSize() {
        val size = Point()
        windowManager.defaultDisplay.getSize(size) // get screen size
        val location = IntArray(2)
        binding.residentSpinnerFrame.getLocationOnScreen(location) // get view coordinates
        val popUpHeight = size.y - location[1] // distance from view to screen bottom
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            resources.getDimension(R.dimen.margin_padding_size_large),
            resources.displayMetrics
        ).toInt()
        listPopupWindow.height = popUpHeight - margin // add margin from bottom
    }
}
