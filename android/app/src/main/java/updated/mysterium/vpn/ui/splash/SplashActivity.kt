package updated.mysterium.vpn.ui.splash

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySplashBinding
import network.mysterium.vpn.databinding.PopUpNewVersionBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.App
import updated.mysterium.vpn.analitics.AnalyticEvent
import updated.mysterium.vpn.analitics.AnalyticWrapper
import updated.mysterium.vpn.common.network.NetworkUtil
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.create.account.CreateAccountActivity
import updated.mysterium.vpn.ui.onboarding.OnboardingActivity
import updated.mysterium.vpn.ui.prepare.top.up.PrepareTopUpActivity
import updated.mysterium.vpn.ui.terms.TermsOfUseActivity

class SplashActivity : BaseActivity() {

    private companion object {
        const val PLAY_MARKET_INSTALLED = "market://details?id="
        const val PLAY_MARKET_NOT_INSTALLED = "https://play.google.com/store/apps/details?id="
    }

    private lateinit var binding: ActivitySplashBinding
    private val balanceViewModel: BalanceViewModel by inject()
    private val viewModel: SplashViewModel by inject()
    private val analyticWrapper: AnalyticWrapper by inject()
    private var isVpnPermissionGranted = false
    private var newVersionPopUpDialog: AlertDialog? = null
    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyDarkMode()
        ensureVpnServicePermission()
        subscribeViewModel()
        setUpPushyNotifications()
    }

    override fun retryLoading() {
        if (isVpnPermissionGranted) {
            checkForGoogleMarketUpdates {
                startLoading()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isVpnPermissionGranted) {
            checkForGoogleMarketUpdates {
                startLoading()
            }
        }
    }

    private fun subscribeViewModel() {
        viewModel.navigateForward.observe(this, {
            establishConnectionListeners()
            navigateForward()
        })
        viewModel.preloadFinished.observe(this, {
            viewModel.initRepository()
        })
    }

    private fun applyDarkMode() {
        when (viewModel.getUserSavedMode()) {
            null -> { // default system theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
            true -> { // user choose dark theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            else -> { // user choose light theme
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
        }
    }

    private fun checkForGoogleMarketUpdates(afterAction: () -> Unit) {
        if (BuildConfig.DEBUG) {
            afterAction.invoke()
        } else {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    showNewVersionAvailablePopUp()
                } else {
                    afterAction.invoke()
                }
            }
            appUpdateManager.appUpdateInfo.addOnFailureListener {
                showPlayMarketErrorToast()
                finish()
            }
        }
    }

    private fun setUpPushyNotifications() {
        pushyNotifications.register {
            val lastCurrency = viewModel.getLastCryptoCurrency()
            if (lastCurrency == null) {
                pushyNotifications.subscribe(PushyTopic.PAYMENT_FALSE)
            } else {
                pushyNotifications.unsubscribe(PushyTopic.PAYMENT_FALSE)
                pushyNotifications.subscribe(PushyTopic.PAYMENT_TRUE)
                pushyNotifications.subscribe(lastCurrency)
            }
        }
        pushyNotifications.listen()
    }

    private fun navigateForward() {
        trackDeviceToken()
        when {
            !viewModel.isUserAlreadyLogin() -> {
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            !viewModel.isTermsAccepted() -> {
                startActivity(Intent(this, TermsOfUseActivity::class.java))
            }
            viewModel.isTopUpFlowShown() -> {
                navigateToConnectionOrHome()
            }
            viewModel.isAccountCreated() -> {
                val intent = Intent(this, PrepareTopUpActivity::class.java).apply {
                    putExtra(PrepareTopUpActivity.IS_NEW_USER_KEY, viewModel.isNewUser())
                }
                startActivity(intent)
            }
            viewModel.isTermsAccepted() -> {
                startActivity(Intent(this, CreateAccountActivity::class.java))
            }
        }
        finish()
    }

    private fun trackDeviceToken() {
        pushyNotifications.deviceToken?.let {
            analyticWrapper.track(AnalyticEvent.NEW_PUSHY_DEVICE, it)
        }
    }

    private fun ensureVpnServicePermission() {
        val vpnServiceIntent = VpnService.prepare(this)
        if (vpnServiceIntent == null) {
            isVpnPermissionGranted = true
            checkForGoogleMarketUpdates {
                startLoading()
            }
        } else {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    isVpnPermissionGranted = true
                    checkForGoogleMarketUpdates {
                        startLoading()
                    }
                } else {
                    showPermissionErrorToast()
                    finish()
                }
            }.launch(vpnServiceIntent)
        }
    }

    private fun showNewVersionAvailablePopUp() {
        if (newVersionPopUpDialog == null) {
            val bindingPopUp = PopUpNewVersionBinding.inflate(layoutInflater)
            newVersionPopUpDialog = createPopUp(bindingPopUp.root, false)
            bindingPopUp.updateButton.setOnClickListener {
                newVersionPopUpDialog = null
                newVersionPopUpDialog?.dismiss()
                openPlayMarket()
            }
            newVersionPopUpDialog?.show()
        }
    }

    private fun showPlayMarketErrorToast() {
        Toast.makeText(
            this,
            getString(R.string.error_play_account),
            Toast.LENGTH_LONG
        ).apply {
            (view.findViewById<View>(android.R.id.message) as TextView).gravity = Gravity.CENTER
        }.show()
    }

    private fun showPermissionErrorToast() {
        Toast.makeText(
            this,
            getString(R.string.error_vpn_permission),
            Toast.LENGTH_LONG
        ).apply {
            (view.findViewById<View>(android.R.id.message) as TextView).gravity = Gravity.CENTER
        }.show()
    }

    private fun startLoading() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            init()
        } else {
            if (
                connectionState == ConnectionState.CONNECTED ||
                connectionState == ConnectionState.ON_HOLD ||
                connectionState == ConnectionState.CONNECTING ||
                connectionState == ConnectionState.IP_NOT_CHANGED
            ) {
                init()
            } else {
                wifiNetworkErrorPopUp()
            }
        }
    }

    private fun init() {
        val deferredMysteriumCoreService = App.getInstance(this).deferredMysteriumCoreService
        balanceViewModel.initDeferredNode(deferredMysteriumCoreService)
        viewModel.startLoading(deferredMysteriumCoreService)
    }

    private fun openPlayMarket() {
        // Exception will be thrown if the Play Store is not installed on the target device.
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_MARKET_INSTALLED + packageName)))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_MARKET_NOT_INSTALLED + packageName)))
        }
    }
}
