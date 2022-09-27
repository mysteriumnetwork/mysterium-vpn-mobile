package updated.mysterium.vpn.ui.pop.up

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import network.mysterium.vpn.databinding.PopUpSmthWentWrongBinding

class PopUpSmthWentWrong(layoutInflater: LayoutInflater) {

    var bindingPopUp = PopUpSmthWentWrongBinding.inflate(layoutInflater)
    var onRetryAction: (() -> Unit)? = null
    private lateinit var dialog: AlertDialog

    fun retryAction(onRetryAction: () -> Unit) {
        this.onRetryAction = onRetryAction
    }

    fun setDialog(alertDialog: AlertDialog) {
        dialog = alertDialog
    }

    fun setUp() {
        bindingPopUp.apply {
            retryButton.setOnClickListener {
                onRetryAction?.invoke()
                dialog.dismiss()
            }
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}
