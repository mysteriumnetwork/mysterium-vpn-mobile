package updated.mysterium.vpn.ui.base

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import network.mysterium.vpn.databinding.PopUpInsufficientFundsBinding
import network.mysterium.vpn.databinding.PopUpTopUpAccountBinding
import network.mysterium.vpn.databinding.PopUpWiFiErrorBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.ui.connection.ConnectionActivity
import updated.mysterium.vpn.ui.custom.view.ConnectionToolbar
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    protected var connectionStateToolbar: ConnectionToolbar? = null
    protected val baseViewModel: BaseViewModel by inject()
    protected var isInternetAvailable = false
    protected var connectionState = ConnectionState.NOTCONNECTED
    private val dialogs = emptyList<Dialog>().toMutableList()
    private var insufficientFoundsDialog: AlertDialog? = null
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel.checkInternetConnection()
        alertDialogBuilder = AlertDialog.Builder(this)
        subscribeViewModel()
    }

    override fun onResume() {
        super.onResume()
        checkUserLocale()
        baseViewModel.checkCurrentConnection()
    }

    override fun onPause() {
        closeAllPopUps()
        super.onPause()
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

    fun wifiNetworkErrorPopUp() {
        val bindingPopUp = PopUpWiFiErrorBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.retryButton.setOnClickListener {
            dialog.dismiss()
            baseViewModel.checkInternetConnection()
        }
        dialog.show()
    }

    fun insufficientFundsPopUp(onContinueAction: (() -> Unit)? = null) {
        val bindingPopUp = PopUpInsufficientFundsBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.topUpButton.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
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

    fun navigateToConnectionOrHome() {
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
        startActivity(intent)
    }

    protected open fun protectedConnection() {
        connectionStateToolbar?.protectedState(true)
    }

    private fun unprotectedConnection() {
        connectionStateToolbar?.unprotectedState()
    }

    private fun subscribeViewModel() {
        baseViewModel.balanceRunningOut.observe(this, {
            balanceRunningOutPopUp()
        })
        baseViewModel.connectionState.observe(this, {
            connectionState = it
            if (it == ConnectionState.CONNECTED || it == ConnectionState.ON_HOLD) {
                isHintAlreadyShown()
                protectedConnection()
            } else {
                unprotectedConnection()
            }
        })
        baseViewModel.insufficientFunds.observe(this, {
            insufficientFundsPopUp()
        })
        baseViewModel.isInternetNotAvailable.observe(this, { isAvailable ->
            isInternetAvailable = isAvailable
            if (!isAvailable) {
                wifiNetworkErrorPopUp()
            } else {
                retryLoading()
            }
        })
    }

    private fun isHintAlreadyShown() {
        if (!baseViewModel.isHintAlreadyShown()) {
            showConnectionHint()
        }
    }

    private fun balanceRunningOutPopUp() {
        if (insufficientFoundsDialog == null) {
            val bindingPopUp = PopUpTopUpAccountBinding.inflate(layoutInflater)
            insufficientFoundsDialog = createPopUp(bindingPopUp.root, true)
            bindingPopUp.topUpButton.setOnClickListener {
                insufficientFoundsDialog?.dismiss()
                insufficientFoundsDialog = null
                startActivity(Intent(this, TopUpAmountActivity::class.java))
            }
            bindingPopUp.continueButton.setOnClickListener {
                insufficientFoundsDialog?.dismiss()
                insufficientFoundsDialog = null
            }
            insufficientFoundsDialog?.show()
        }
    }

    private fun checkUserLocale() {
        baseViewModel.initUserLocaleLanguage()?.let { languageForApply ->
            Locale.setDefault(Locale(languageForApply))
            resources.configuration.setLocale(Locale(languageForApply))
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        }
    }
}
