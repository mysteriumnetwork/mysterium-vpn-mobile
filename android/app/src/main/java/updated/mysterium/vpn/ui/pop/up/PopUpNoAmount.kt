package updated.mysterium.vpn.ui.pop.up

import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import network.mysterium.vpn.databinding.PopUpNoAmountBinding

class PopUpNoAmount(layoutInflater: LayoutInflater) {

    var bindingPopUp = PopUpNoAmountBinding.inflate(layoutInflater)
    var onTryAgainAction: (() -> Unit)? = null
    var dialog: AlertDialog? = null

    fun setUp() {
        bindingPopUp.apply {
            tryAgainButton.setOnClickListener {
                onTryAgainAction?.invoke()
                dialog?.dismiss()
            }
            closeButton.setOnClickListener {
                dialog?.dismiss()
            }
        }
    }

}
