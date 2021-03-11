package updated.mysterium.vpn.ui.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityMenuBinding
import network.mysterium.vpn.databinding.SpinnerLanguageSelectorBinding
import updated.mysterium.vpn.model.menu.MenuItem
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity

class MenuActivity : AppCompatActivity() {

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
        viewSelectorBinding.apply {
            languageSelector.setOnClickListener {
                viewSelectorBinding.spinner.performClick()
            }
            spinner.adapter = ArrayAdapter(
                this@MenuActivity,
                R.layout.item_spinner_languages,
                resources.getStringArray(R.array.menu_languages).toList(),
            )
        }
        binding.manualConnectToolbar.setLeftView(viewSelectorBinding.languageSelector)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun inflateGridLayout() {
        binding.menuRecyclerView.adapter = menuGridAdapter
        binding.menuRecyclerView.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        menuGridAdapter.replaceAll(MENU_ITEMS)
    }

    private fun inflateAppVersion() {
        val info = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        binding.appVersionTextView.text = getString(R.string.menu_app_version, info.versionName)
    }

    private fun bindsAction() {
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
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
                    // TODO("Implement navigation to Profile")
                }
                2 -> menuItem.onItemClickListener = {
                    // TODO("Implement navigation to Wallet")
                }
                3 -> menuItem.onItemClickListener = {
                    // TODO("Implement navigation to Monitoring")
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
