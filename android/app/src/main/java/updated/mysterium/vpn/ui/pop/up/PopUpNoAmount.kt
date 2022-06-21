package updated.mysterium.vpn.ui.pop.up

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import network.mysterium.vpn.databinding.PopUpNoAmountBinding

class PopUpNoAmount(layoutInflater: LayoutInflater) {

    var bindingPopUp = PopUpNoAmountBinding.inflate(layoutInflater)
    private var onTryAgainAction: (() -> Unit)? = null
    private lateinit var dialog: AlertDialog

    fun tryAgainAction(onTryAgainAction: () -> Unit) {
        this.onTryAgainAction = onTryAgainAction
    }

    fun setDialog(alertDialog: AlertDialog) {
        dialog = alertDialog
    }

    fun setUp() {
        bindingPopUp.apply {
            tryAgainButton.setOnClickListener {
                onTryAgainAction?.invoke()
                dialog.dismiss()
            }
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

}
