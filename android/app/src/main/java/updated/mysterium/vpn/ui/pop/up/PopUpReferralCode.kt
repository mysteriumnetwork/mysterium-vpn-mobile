package updated.mysterium.vpn.ui.pop.up

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.PopUpReferralCodeBinding
import updated.mysterium.vpn.common.extensions.hideKeyboard
import updated.mysterium.vpn.common.extensions.isValidRegistrationToken

class PopUpReferralCode(layoutInflater: LayoutInflater) {

    var bindingPopUp = PopUpReferralCodeBinding.inflate(layoutInflater)
    private var onApplyAction: ((String) -> Unit)? = null
    private lateinit var dialog: AlertDialog

    fun applyAction(onApplyAction: (String) -> Unit) {
        this.onApplyAction = onApplyAction
    }

    fun setDialog(alertDialog: AlertDialog) {
        dialog = alertDialog
    }

    fun setUp() {
        bindingPopUp.apply {
            applyButton.setOnClickListener {
                val token = bindingPopUp.registrationTokenEditText.text.toString()
                if (token.isEmpty()) {
                    showRegistrationTokenRewardError(dialog.context.getString(R.string.pop_up_referral_empty_error))
                } else if (!token.isValidRegistrationToken()) {
                    showRegistrationTokenRewardError()
                } else {
                    onApplyAction?.invoke(token)
                }
            }
            bindingPopUp.registrationTokenEditText.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        bindingPopUp.registrationTokenEditText.text?.clear()
                        clearErrorState()
                    }
                }
            bindingPopUp.registrationTokenEditText.doOnTextChanged { _, _, _, _ ->
            }
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    fun showRewardAmount(rewardAmount: Double) {
        bindingPopUp.rewardAmount.visibility = View.VISIBLE
        bindingPopUp.rewardAmount.text = rewardAmount.toString()
        bindingPopUp.tokenNotWorkingImageView.visibility = View.INVISIBLE
    }

    fun hideRewardAmount() {
        bindingPopUp.tokenNotWorkingImageView.visibility = View.VISIBLE
        bindingPopUp.rewardAmount.visibility = View.INVISIBLE
    }

    fun showRegistrationTokenRewardError(errorMessage: String? = null) {
        bindingPopUp.errorText.text =
            errorMessage ?: dialog.context.getString(R.string.pop_up_referral_invalid_error)
        bindingPopUp.errorText.visibility = View.VISIBLE
        bindingPopUp.registrationTokenEditText.apply {
            background = ContextCompat.getDrawable(
                dialog.context, R.drawable.shape_wrong_password
            )
            text?.clear()
            hint = ""
            clearFocus()
            hideKeyboard()
        }
    }

    private fun clearErrorState() {
        bindingPopUp.errorText.visibility = View.INVISIBLE
        bindingPopUp.registrationTokenEditText.apply {
            background = ContextCompat.getDrawable(
                dialog.context, R.drawable.shape_password_field
            )
            hint = dialog.context.getString(R.string.pop_up_referral_hint)
        }
    }

}
