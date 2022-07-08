package updated.mysterium.vpn.ui.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityMenuBinding
import network.mysterium.vpn.databinding.SpinnerLanguageSelectorBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.onItemSelected
import updated.mysterium.vpn.common.languages.LanguagesUtil
import updated.mysterium.vpn.common.localisation.LocaleHelper
import updated.mysterium.vpn.model.menu.MenuItem
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.monitoring.MonitoringActivity
import updated.mysterium.vpn.ui.profile.ProfileActivity
import updated.mysterium.vpn.ui.report.issue.ReportIssueActivity
import updated.mysterium.vpn.ui.settings.SettingsActivity
import updated.mysterium.vpn.ui.terms.TermsOfUseActivity
import updated.mysterium.vpn.ui.wallet.WalletActivity

class MenuActivity : BaseActivity() {

    private companion object {
        val MENU_ITEMS = listOf(
            MenuItem(
                iconResId = R.drawable.menu_icon_home,
                titleResId = R.string.menu_item_home_title
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_wallet,
                titleResId = R.string.menu_item_wallet_title
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_profile,
                titleResId = R.string.menu_item_profile_title
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_monitoring,
                titleResId = R.string.menu_item_monitoring_title,
                subTitleResId = R.string.menu_item_monitoring_subtitle
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_settings,
                titleResId = R.string.menu_list_item_settings,
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_referral_deactivated,
                titleResId = R.string.menu_item_referral_title,
                subTitleResId = R.string.menu_item_referral_subtitle,
                isActive = false
            )
        )
    }

    private lateinit var binding: ActivityMenuBinding
    private val balanceViewModel: BalanceViewModel by inject()
    private val viewModel: MenuViewModel by inject()
    private val menuGridAdapter = MenuGridAdapter()
    private var isLanguageSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    override fun onDestroy() {
        isLanguageSelected = false
        super.onDestroy()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        balanceViewModel.requestBalanceChange()
        inflateCustomToolbarView()
        inflateGridLayout()
        inflateAppVersion()
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this) {
            val balance = getString(R.string.menu_current_balance, it)
            MENU_ITEMS[1].dynamicSubtitle = balance // Added balance to wallet item
            menuGridAdapter.replaceAll(MENU_ITEMS)
        }
    }

    private fun inflateCustomToolbarView() {
        val viewSelectorBinding = SpinnerLanguageSelectorBinding.inflate(layoutInflater)
        val languagesList = resources.getStringArray(R.array.menu_languages).toMutableList()
        val spinnerAdapter = SpinnerArrayAdapter(
            this@MenuActivity,
            R.layout.item_spinner_languages,
            languagesList
        )
        viewSelectorBinding.apply {
            languageSelector.setOnClickListener {
                isLanguageSelected = true
                viewSelectorBinding.spinner.performClick()
            }
            spinner.adapter = spinnerAdapter
            spinner.setOnTouchListener { _, _ ->
                isLanguageSelected = true
                viewSelectorBinding.spinner.performClick()
            }
            spinner.onItemSelected {
                setLocale(LanguagesUtil.getCountryCodeByIndex(it))
                viewModel.saveUserSelectedLanguage(it)
                spinnerAdapter.selectedPosition = it
                if (isLanguageSelected) {
                    isLanguageSelected = false
                    viewModel.userManualSelect(false)
                    recreate()
                }
            }
            spinner.setSelection(viewModel.getUserLanguageIndex())
        }
        binding.manualConnectToolbar.setRightView(viewSelectorBinding.languageSelector)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun inflateGridLayout() {
        binding.menuRecyclerView.adapter = menuGridAdapter
        binding.menuRecyclerView.layoutManager =
            GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        menuGridAdapter.replaceAll(MENU_ITEMS)
    }

    private fun inflateAppVersion() {
        val version = "${BuildConfig.VERSION_CODE}.${BuildConfig.VERSION_NAME}"
        val formattedVersion = getString(R.string.report_issue_app_version_template, version)
        binding.appVersionTextView.text = getString(R.string.menu_app_version, formattedVersion)
    }

    private fun setLocale(languageCode: String) {
        LocaleHelper.setLocale(this, languageCode)
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            navigateToConnectionIfConnectedOrHome()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionIfConnectedOrHome()
        }
        binding.helpButton.setOnClickListener {
            composeEmail()
        }
        binding.reportButton.setOnClickListener {
            startActivity(Intent(this, ReportIssueActivity::class.java))
        }
        binding.termsTextView.setOnClickListener {
            startActivity(Intent(this, TermsOfUseActivity::class.java))
        }
        MENU_ITEMS.forEachIndexed { index, menuItem ->
            when (index) {
                0 -> menuItem.onItemClickListener = {
                    navigateToConnectionIfConnectedOrHome()
                }
                1 -> menuItem.onItemClickListener = {
                    startActivity(Intent(this, WalletActivity::class.java))
                }
                2 -> menuItem.onItemClickListener = {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                3 -> menuItem.onItemClickListener = {
                    startActivity(Intent(this, MonitoringActivity::class.java))
                }
                4 -> menuItem.onItemClickListener = {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                5 -> menuItem.onItemClickListener = {
                    // TODO("Implement navigation to Referral")
                }
            }
        }
    }

    private fun composeEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.get_help_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.get_help_subject))
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
