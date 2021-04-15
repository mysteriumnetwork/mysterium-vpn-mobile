package updated.mysterium.vpn.ui.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import network.mysterium.vpn.databinding.PopUpTopUpAccountBinding
import network.mysterium.vpn.databinding.PopUpWiFiErrorBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.connection.ConnectionUtil
import updated.mysterium.vpn.model.manual.connect.ConnectionState
import updated.mysterium.vpn.ui.custom.view.ConnectionToolbar
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

abstract class BaseActivity : AppCompatActivity() {

    protected var connectionStateToolbar: ConnectionToolbar? = null
    private val dialogs = emptyList<Dialog>().toMutableList()
    private val viewModel: BaseViewModel by inject()
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alertDialogBuilder = AlertDialog.Builder(this)
        subscribeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkCurrentConnection()
        handleInternetConnection()
    }

    override fun onPause() {
        dialogs.forEach {
            it.dismiss()
        }
        super.onPause()
    }

    open fun retryLoading() {
        // Override in activity for handle retry loading click
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

    fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork
        val activeNetworksCapabilities = connectivityManager.getNetworkCapabilities(networkCapabilities)
        return if (activeNetworksCapabilities != null) {
            if (ConnectionUtil.isConnectionWithoutVPNAvailable(activeNetworksCapabilities)) {
                true // connection available
            } else {
                // check connection with VPN
                ConnectionUtil.isConnectionWithVPNAvailable(activeNetworksCapabilities)
            }
        } else {
            // can't get info about connection
            false
        }
    }

    fun wifiNetworkErrorPopUp() {
        val bindingPopUp = PopUpWiFiErrorBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, false)
        bindingPopUp.retryButton.setOnClickListener {
            dialog.dismiss()
            retryLoading()
            handleInternetConnection()
        }
        dialog.show()
    }

    protected open fun protectedConnection() {
        connectionStateToolbar?.protectedState(true)
    }

    private fun unprotectedConnection() {
        connectionStateToolbar?.unprotectedState()
    }

    private fun subscribeViewModel() {
        viewModel.balanceRunningOut.observe(this, {
            balanceRunningOutPopUp()
        })
        viewModel.connectionState.observe(this, {
            if (it == ConnectionState.CONNECTED) {
                protectedConnection()
            } else {
                unprotectedConnection()
            }
        })
    }

    private fun handleInternetConnection() {
        if (!isInternetAvailable()) {
            wifiNetworkErrorPopUp()
        }
    }

    private fun balanceRunningOutPopUp() {
        val bindingPopUp = PopUpTopUpAccountBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.topUpButton.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
        }
        bindingPopUp.continueButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
