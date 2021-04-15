package updated.mysterium.vpn.ui.settings

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.widget.ListPopupWindow
import network.mysterium.ui.onItemSelected
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySettingsBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.countries.CountriesUtil
import updated.mysterium.vpn.model.settings.DnsOption
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.custom.view.LongListPopUpWindow
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.menu.SpinnerArrayAdapter

class SettingsActivity : BaseActivity() {

    private companion object {
        const val TAG = "SettingsActivity"
        val DNS_OPTIONS = listOf(
            DnsOption(
                translatableValueResId = R.string.settings_dns_default,
                backendValue = "auto"
            ),
            DnsOption(
                translatableValueResId = R.string.settings_dns_system,
                backendValue = "system"
            ),
            DnsOption(
                translatableValueResId = R.string.settings_dns_provider,
                backendValue = "provider"
            ),
            DnsOption(
                translatableValueResId = R.string.settings_dns_cloudflare,
                backendValue = "cloudflare" // TODO("Replace when it will implemented on back")
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

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        setUpDnsSpinner()
        setUpResidentCountryList()
        checkPreviousDnsOption()
        checkPreviousResidentCountry()
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onConnectClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
        binding.residentSpinnerFrame.setOnClickListener {
            listPopupWindow.show()
        }
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
            result.onSuccess { residentDigitCode ->
                val countriesList = CountriesUtil().getAllCountries()
                val residentCountry = countriesList.find { it.digitCode == residentDigitCode }
                binding.selectedCountry.text = residentCountry?.fullName
            }
            result.onFailure {
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
