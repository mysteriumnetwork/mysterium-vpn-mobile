package updated.mysterium.vpn.ui.base

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.PopUpInsufficientFundsBinding
import network.mysterium.vpn.databinding.PopUpRetryRegistrationBinding
import network.mysterium.vpn.databinding.PopUpTopUpAccountBinding
import network.mysterium.vpn.databinding.PopUpWiFiErrorBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.localisation.LocaleHelper
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.model.pushy.PushyTopic
import updated.mysterium.vpn.notification.Notifications
import updated.mysterium.vpn.ui.connection.ConnectionActivity
import updated.mysterium.vpn.ui.custom.view.ConnectionToolbar
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.payment.method.PaymentMethodActivity
import updated.mysterium.vpn.ui.top.up.coingate.amount.TopUpAmountActivity
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    protected var connectionStateToolbar: ConnectionToolbar? = null
    protected val baseViewModel: BaseViewModel by inject()
    protected var isInternetAvailable = true
    protected var connectionState = ConnectionState.NOTCONNECTED
    protected val pushyNotifications = Notifications(this)
    private val dialogs = emptyList<Dialog>().toMutableList()
    private var insufficientFoundsDialog: AlertDialog? = null
    private var wifiErrorDialog: AlertDialog? = null
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutDirection()
        baseViewModel.checkInternetConnection()
        alertDialogBuilder = AlertDialog.Builder(this)
        subscribeViewModel()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleHelper.onAttach(
                context = newBase,
                currentLanguage = baseViewModel.getUserCurrentLanguageCode()
            )
        )
    }

    override fun onResume() {
        super.onResume()
        baseViewModel.checkCurrentConnection()
    }

    override fun onPause() {
        closeAllPopUps()
        super.onPause()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    open fun retryLoading() {
        // Override in activity for handle retry loading click
    }

    open fun showConnectionHint() {
        // Override in activity for show connection hint
    }

    fun initToolbar(connectionToolbar: ConnectionToolbar) {
        connectionStateToolbar = connectionToolbar
    }

    fun createPopUp(popUpView: View, cancelable: Boolean): AlertDialog {
        alertDialogBuilder.apply {
            setView(popUpView)
            setCancelable(cancelable)
            create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.setLayout(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
            }
        }
        val dialog = alertDialogBuilder.create()
        dialog.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
        dialogs.add(dialog)
        return dialog
    }

    fun detailedErrorPopUp(
        errorMessage: String,
        retryAction: () -> Unit
    ) {
        val bindingPopUp = PopUpRetryRegistrationBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.description.text = errorMessage
        bindingPopUp.title.setText(R.string.pop_up_details_title)
        bindingPopUp.tryAgainButton.setOnClickListener {
            retryAction.invoke()
            dialog.dismiss()
        }
        bindingPopUp.cancelButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    fun wifiNetworkErrorPopUp(retryAction: () -> Unit) {
        if (wifiErrorDialog == null && !isFinishing) {
            val bindingPopUp = PopUpWiFiErrorBinding.inflate(layoutInflater)
            wifiErrorDialog = createPopUp(bindingPopUp.root, false)
            bindingPopUp.retryButton.setOnClickListener {
                wifiErrorDialog?.dismiss()
                wifiErrorDialog = null
                retryAction()
            }
            wifiErrorDialog?.show()
        }
    }

    fun insufficientFundsPopUp(onContinueAction: (() -> Unit)? = null) {
        val bindingPopUp = PopUpInsufficientFundsBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.topUpButton.setOnClickListener {
            navigateToPayment()
        }
        bindingPopUp.continueButton.setOnClickListener {
            dialog.dismiss()
            onContinueAction?.invoke()
        }
        dialog.show()
    }

    fun closeAllPopUps() {
        dialogs.forEach {
            it.dismiss()
        }
    }

    fun navigateToConnectionOrHome(isBackTransition: Boolean = true) {
        val intent = if (
            connectionState == ConnectionState.CONNECTED ||
            connectionState == ConnectionState.CONNECTING ||
            connectionState == ConnectionState.ON_HOLD
        ) {
            Intent(this, ConnectionActivity::class.java)
        } else {
            Intent(this, HomeSelectionActivity::class.java)
        }
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val transitionAnimation = if (isBackTransition) {
            ActivityOptions.makeCustomAnimation(
                applicationContext,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ).toBundle()
        } else {
            ActivityOptions.makeCustomAnimation(
                applicationContext,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            ).toBundle()
        }
        startActivity(intent, transitionAnimation)
    }

    fun establishConnectionListeners() {
        baseViewModel.establishListeners()
    }

    protected open fun protectedConnection() {
        connectionStateToolbar?.protectedState(true)
    }

    private fun unprotectedConnection() {
        connectionStateToolbar?.unprotectedState()
    }

    private fun subscribeViewModel() {
        baseViewModel.balance.observe(this) {
            if (it < BaseViewModel.BALANCE_LIMIT) {
                pushyNotifications.subscribe(PushyTopic.LESS_THEN_HALF_MYST)
            } else {
                pushyNotifications.unsubscribe(PushyTopic.LESS_THEN_HALF_MYST)
            }
        }
        baseViewModel.balanceRunningOut.observe(this) {
            balanceRunningOutPopUp()
        }
        baseViewModel.connectionState.observe(this) {
            connectionState = it
            if (
                it == ConnectionState.CONNECTED ||
                it == ConnectionState.ON_HOLD ||
                it == ConnectionState.IP_NOT_CHANGED
            ) {
                isHintAlreadyShown()
                protectedConnection()
            } else {
                unprotectedConnection()
            }
        }
        baseViewModel.insufficientFunds.observe(this) {
            insufficientFundsPopUp()
        }
        baseViewModel.isInternetAvailable.observe(this) { isAvailable ->
            if (!isAvailable) {
                wifiNetworkErrorPopUp {
                    baseViewModel.checkInternetConnection()
                }
            } else if (!isInternetAvailable) {
                wifiErrorDialog?.dismiss()
                wifiErrorDialog = null
                retryLoading()
            }
            isInternetAvailable = isAvailable
        }
    }

    private fun isHintAlreadyShown() {
        if (!baseViewModel.isHintAlreadyShown()) {
            showConnectionHint()
        }
    }

    private fun balanceRunningOutPopUp() {
        if (insufficientFoundsDialog == null) {
            val bindingPopUp = PopUpTopUpAccountBinding.inflate(layoutInflater)
            insufficientFoundsDialog = createPopUp(bindingPopUp.root, false)
            bindingPopUp.topUpButton.setOnClickListener {
                insufficientFoundsDialog?.dismiss()
                insufficientFoundsDialog = null
                navigateToPayment()
            }
            bindingPopUp.continueButton.setOnClickListener {
                insufficientFoundsDialog?.dismiss()
                insufficientFoundsDialog = null
            }
            insufficientFoundsDialog?.show()
        }
    }

    private fun setLayoutDirection() {
        if (Locale.getDefault().language == "ar") {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
    }

    fun navigateToPayment() {
        baseViewModel.getGateways().observe(this) {
            it.onSuccess { result ->
                val gateways = result.filterNotNull()
                val intent = if (gateways.size == 1) {
                    Intent(this, TopUpAmountActivity::class.java).apply {
                        putExtra(
                            TopUpAmountActivity.PAYMENT_METHOD_EXTRA_KEY,
                            gateways[0].gateway
                        )
                    }
                } else {
                    val gatewayValues = gateways.map { it.gateway }
                    PaymentMethodActivity.newIntent(this, gatewayValues)
                }
                startActivity(intent)
            }
            it.onFailure { error ->
                Log.e(TAG, "getPaymentScreen failed with error ${error.message}")
            }
        }
    }
}
