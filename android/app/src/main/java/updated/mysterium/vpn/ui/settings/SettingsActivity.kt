package updated.mysterium.vpn.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import network.mysterium.ui.onItemSelected
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySettingsBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.settings.DnsOption
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.menu.SpinnerArrayAdapter

class SettingsActivity : BaseActivity() {

    private companion object {
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
    private val balanceViewModel: BalanceViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this, {
            binding.manualConnectToolbar.setBalance(it)
        })
    }

    private fun configure() {
        setUpDnsSpinner()
        checkPreviousDnsOption()
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
    }

    private fun setUpDnsSpinner() {
        val languagesList = DNS_OPTIONS.map { getString(it.translatableValueResId) }
        val spinnerAdapter = SpinnerArrayAdapter(
            this@SettingsActivity,
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
}
