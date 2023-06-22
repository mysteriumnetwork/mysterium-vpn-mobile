package updated.mysterium.vpn.ui.splash

import android.Manifest.permission.POST_NOTIFICATIONS
import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivitySplashBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.App
import updated.mysterium.vpn.analytics.AnalyticEvent
import updated.mysterium.vpn.analytics.mysterium.MysteriumAnalytic
import updated.mysterium.vpn.common.animation.OnAnimationCompletedListener
import updated.mysterium.vpn.common.network.NetworkUtil
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.base.RegistrationViewModel
import updated.mysterium.vpn.ui.create.account.CreateAccountActivity
import updated.mysterium.vpn.ui.onboarding.OnboardingActivity
import updated.mysterium.vpn.ui.prepare.top.up.PrepareTopUpActivity
import updated.mysterium.vpn.ui.private.key.PrivateKeyActivity
import updated.mysterium.vpn.ui.terms.TermsOfUseActivity
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel

class SplashActivity : BaseActivity() {

    companion object {
        const val REDIRECTED_FROM_PUSH_KEY = "REDIRECTED_FROM_PUSH"
    }

    private lateinit var binding: ActivitySplashBinding
    private val balanceViewModel: BalanceViewModel by inject()
    private val viewModel: SplashViewModel by inject()
    private val allNodesViewModel: AllNodesViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()
    private val registrationViewModel: RegistrationViewModel by inject()
    private val analytic: MysteriumAnalytic by inject()
    private var isVpnPermissionGranted = false
    private var isLoadingStarted = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            startLoading()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyDarkMode()
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            isLoadingStarted = savedInstanceState.getBoolean("isLoadingStarted")
        }
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ensureVpnServicePermission()
        subscribeViewModel()
        viewModel.setUpInactiveUserPushyNotifications()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isLoadingStarted", isLoadingStarted)
        super.onSaveInstanceState(outState)
    }

    override fun retryLoading() {
        if (isVpnPermissionGranted) {
            startLoading()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isVpnPermissionGranted) {
            startLoading()
        }
    }

    private fun subscribeViewModel() {
        viewModel.navigateForward.observe(this) {
            allNodesViewModel.launchProposalsPeriodically()
            exchangeRateViewModel.launchPeriodicallyExchangeRate()
            balanceViewModel.requestBalanceChange()
            establishConnectionListeners()
            analytic.trackEvent(AnalyticEvent.STARTUP.eventName)
        }
        viewModel.preloadFinished.observe(this) {
            viewModel.initRepository()
        }
        viewModel.nodeStartingError.observe(this) {
            wifiNetworkErrorPopUp {
                isLoadingStarted = false
                init()
            }
        }

        registrationViewModel.identityRegistrationResult.observe(this) { isRegistered ->
            val redirectedFromPush = intent?.extras?.getBoolean(REDIRECTED_FROM_PUSH_KEY) ?: false
            if (isRegistered && !redirectedFromPush) {
                navigateToConnectionIfConnectedOrHome(isBackTransition = false)
                finish()
            } else {
                navigateForward(redirectedFromPush)
            }
        }
        registrationViewModel.identityRegistrationError.observe(this) {
            detailedErrorPopUp {
                registrationViewModel.tryRegisterIdentity()
            }
        }

        lifecycleScope.launchWhenStarted {
            analytic.eventTracked.collect { event ->
                if (event == AnalyticEvent.STARTUP.eventName) {
                    registrationViewModel.tryRegisterIdentity()
                }
            }
        }
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

    private fun navigateForward(redirectedFromPush: Boolean) {
        when {
            !viewModel.isUserAlreadyLogin() -> {
                navigateToOnboarding()
            }
            !viewModel.isTermsAccepted() -> {
                navigateToTerms()
            }
            viewModel.isTopUpFlowShown() && !redirectedFromPush -> {
                navigateToConnectionIfConnectedOrHome(isBackTransition = false)
            }
            redirectedFromPush -> {
                navigateToConnectionIfBalanceOrHome()
            }
            viewModel.isAccountCreated() -> {
                navigateToTopUp()
            }
            viewModel.isTermsAccepted() -> {
                navigateToCreateAccount()
            }
        }
    }

    private fun ensureVpnServicePermission() {
        val vpnServiceIntent = VpnService.prepare(this)
        if (vpnServiceIntent == null) {
            isVpnPermissionGranted = true
            askNotificationsPermissions()
        } else {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    isVpnPermissionGranted = true
                    askNotificationsPermissions()
                } else {
                    showPermissionErrorToast()
                    finish()
                }
            }.launch(vpnServiceIntent)
        }
    }

    private fun askNotificationsPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startLoading()
            return
        }
        requestPermissionLauncher.launch(POST_NOTIFICATIONS)
    }

    private fun showPermissionErrorToast() {
        Toast.makeText(
            this,
            getString(R.string.error_vpn_permission),
            Toast.LENGTH_LONG
        ).apply {
            setGravity(Gravity.CENTER, 0, 0)
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
                wifiNetworkErrorPopUp {
                    baseViewModel.checkInternetConnection()
                }
            }
        }
    }

    private fun init() {
        if (!isLoadingStarted) {
            isLoadingStarted = true
            val deferredMysteriumCoreService = App.getInstance(this).deferredMysteriumCoreService
            balanceViewModel.initDeferredNode(deferredMysteriumCoreService)
            viewModel.startLoading(deferredMysteriumCoreService)
        }
    }

    private fun navigateToTopUp() {
        val intents = arrayOf(
            Intent(this, CreateAccountActivity::class.java),
            Intent(this, PrivateKeyActivity::class.java),
            Intent(this, PrepareTopUpActivity::class.java)
        )
        startActivities(intents)
        finish()
    }

    private fun navigateToOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun navigateToTerms() {
        val intent = Intent(this, TermsOfUseActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun navigateToCreateAccount() {
        val intent = Intent(this, CreateAccountActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}
