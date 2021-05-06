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
import updated.mysterium.vpn.ui.custom.view.ConnectionToolbar
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

abstract class BaseActivity : AppCompatActivity() {

    protected var connectionStateToolbar: ConnectionToolbar? = null
    protected val baseViewModel: BaseViewModel by inject()
    protected var isInternetAvailable = false
    protected var connectionState = ConnectionState.NOTCONNECTED
    private val dialogs = emptyList<Dialog>().toMutableList()
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseViewModel.checkInternetConnection()
        alertDialogBuilder = AlertDialog.Builder(this)
        subscribeViewModel()
    }

    override fun onResume() {
        super.onResume()
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
            retryLoading()
            baseViewModel.checkInternetConnection()
        }
        dialog.show()
    }

    fun insufficientFundsPopUp() {
        val bindingPopUp = PopUpInsufficientFundsBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.topUpButton.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
        }
        bindingPopUp.continueButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun closeAllPopUps() {
        dialogs.forEach {
            it.dismiss()
        }
    }

    protected open fun protectedConnection() {
        connectionStateToolbar?.protectedState(true)
    }

    private fun unprotectedConnection() {
        connectionStateToolbar?.unprotectedState()
    }

    private fun subscribeViewModel() {
        baseViewModel.balanceRunningOut.observe(this, {
            balanceRunningOutPopUp(it)
        })
        baseViewModel.connectionState.observe(this, {
            connectionState = it
            if (it == ConnectionState.CONNECTED) {
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

    private fun balanceRunningOutPopUp(isFirstWarning: Boolean) {
        val bindingPopUp = PopUpTopUpAccountBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.topUpButton.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
        }
        bindingPopUp.continueButton.setOnClickListener {
            if (isFirstWarning) {
                baseViewModel.firstWarningBalanceShown()
            } else {
                baseViewModel.secondWarningBalanceShown()
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}
