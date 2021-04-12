package updated.mysterium.vpn.ui.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.intercom.android.sdk.Intercom
import network.mysterium.ui.onItemSelected
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityMenuBinding
import network.mysterium.vpn.databinding.SpinnerLanguageSelectorBinding
import updated.mysterium.vpn.model.menu.MenuItem
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
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
                iconResId = R.drawable.menu_icon_profile,
                titleResId = R.string.menu_item_profile_title
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_wallet,
                titleResId = R.string.menu_item_wallet_title,
                subTitleResId = R.string.menu_item_wallet_subtitle
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_monitoring,
                titleResId = R.string.menu_item_monitoring_title,
                subTitleResId = R.string.menu_item_monitoring_subtitle
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_black_list,
                titleResId = R.string.menu_item_black_list_title,
                subTitleResId = R.string.menu_item_black_list_subtitle
            ),
            MenuItem(
                iconResId = R.drawable.menu_icon_referral,
                titleResId = R.string.menu_item_referral_title
            )
        )
    }

    private lateinit var binding: ActivityMenuBinding
    private val menuGridAdapter = MenuGridAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        inflateLayout()
        bindsAction()
    }

    private fun inflateLayout() {
        inflateCustomToolbarView()
        inflateGridLayout()
        inflateAppVersion()
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
                viewSelectorBinding.spinner.performClick()
            }
            spinner.adapter = spinnerAdapter
            spinner.onItemSelected {
                spinnerAdapter.selectedPosition = it
            }
        }
        binding.manualConnectToolbar.setRightView(viewSelectorBinding.languageSelector)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun inflateGridLayout() {
        binding.menuRecyclerView.adapter = menuGridAdapter
        binding.menuRecyclerView.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        menuGridAdapter.replaceAll(MENU_ITEMS)
    }

    private fun inflateAppVersion() {
        val version = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
        val formattedVersion = getString(R.string.report_issue_app_version_template, version)
        binding.appVersionTextView.text = getString(R.string.menu_app_version, formattedVersion)
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
        binding.helpButton.setOnClickListener {
            Intercom.client().displayMessenger()
        }
        binding.reportButton.setOnClickListener {
            startActivity(Intent(this, ReportIssueActivity::class.java))
        }
        binding.termsTextView.setOnClickListener {
            startActivity(Intent(this, TermsOfUseActivity::class.java))
        }
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        MENU_ITEMS.forEachIndexed { index, menuItem ->
            when (index) {
                0 -> menuItem.onItemClickListener = {
                    val intent = Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                }
                1 -> menuItem.onItemClickListener = {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                2 -> menuItem.onItemClickListener = {
                    startActivity(Intent(this, WalletActivity::class.java))
                }
                3 -> menuItem.onItemClickListener = {
                    startActivity(Intent(this, MonitoringActivity::class.java))
                }
                4 -> menuItem.onItemClickListener = {
                    // TODO("Implement navigation to Black list")
                }
                5 -> menuItem.onItemClickListener = {
                    // TODO("Implement navigation to Referral")
                }
            }
        }
    }
}
