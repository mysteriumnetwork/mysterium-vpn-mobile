package updated.mysterium.vpn.ui.pop.up

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.PopUpDownloadKeyBinding
import updated.mysterium.vpn.common.extensions.hideKeyboard
import updated.mysterium.vpn.common.extensions.isValidPassword
import updated.mysterium.vpn.common.extensions.setSelectionChangedListener

class PopUpDownloadKey(layoutInflater: LayoutInflater) {

    var isPasswordVisible = false
    var isRepeatPasswordVisible = false
    var bindingPopUp = PopUpDownloadKeyBinding.inflate(layoutInflater)
    private var onDownloadAction: ((String) -> Unit)? = null
    private lateinit var dialog: AlertDialog

    fun downloadAction(onDownloadAction: (String) -> Unit) {
        this.onDownloadAction = onDownloadAction
    }

    fun setDialog(alertDialog: AlertDialog) {
        dialog = alertDialog
    }

    fun setUp() {
        bindingPopUp.apply {
            downloadButton.setOnClickListener {
                val passphrase = bindingPopUp.passwordEditText.text.toString()
                if (passphrase.isValidPassword()) {
                    val repeatPassphrase = bindingPopUp.repeatPasswordEditText.text.toString()
                    if (passphrase == repeatPassphrase) {
                        onDownloadAction?.invoke(passphrase)
                        dialog.dismiss()
                    } else {
                        setErrorState(dialog.context.getString(R.string.pop_up_private_key_match_error))
                    }
                } else {
                    setErrorState(dialog.context.getString(R.string.pop_up_private_key_valid_error))
                }
            }
            passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    bindingPopUp.passwordEditText.hint = dialog
                        .context
                        .getString(R.string.pop_up_password_account_hint)
                    bindingPopUp.repeatPasswordEditText.hint = dialog
                        .context
                        .getString(R.string.pop_up_repeat_password_account_hint)
                    if (isErrorState()) clearErrorState()
                }
            }
            repeatPasswordEditText.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        bindingPopUp.passwordEditText.hint = dialog
                            .context
                            .getString(R.string.pop_up_password_account_hint)
                        bindingPopUp.repeatPasswordEditText.hint = dialog
                            .context
                            .getString(R.string.pop_up_repeat_password_account_hint)
                        if (isErrorState()) clearErrorState()
                    }
                }
            passwordEditText.doOnTextChanged { _, _, _, _ ->
                if (isPasswordVisible) {
                    bindingPopUp.showPasswordImageView.visibility = View.INVISIBLE
                    bindingPopUp.hidePasswordImageView.visibility = View.VISIBLE
                } else {
                    bindingPopUp.showPasswordImageView.visibility = View.VISIBLE
                    bindingPopUp.hidePasswordImageView.visibility = View.INVISIBLE
                }
                clearErrorState()
            }
            repeatPasswordEditText.doOnTextChanged { _, _, _, _ ->
                if (isRepeatPasswordVisible) {
                    bindingPopUp.showRepeatPasswordImageView.visibility = View.INVISIBLE
                    bindingPopUp.hideRepeatPasswordImageView.visibility = View.VISIBLE
                } else {
                    bindingPopUp.showRepeatPasswordImageView.visibility = View.VISIBLE
                    bindingPopUp.hideRepeatPasswordImageView.visibility = View.INVISIBLE
                }
                clearErrorState()
            }
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
            var position = 0
            bindingPopUp.passwordEditText.setSelectionChangedListener {
                position = it
            }
            bindingPopUp.repeatPasswordEditText.setSelectionChangedListener {
                position = it
            }
            showPasswordImageView.setOnClickListener {
                val oldPosition = position
                isPasswordVisible = true
                bindingPopUp.apply {
                    passwordEditText.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    showPasswordImageView.visibility = View.INVISIBLE
                    hidePasswordImageView.visibility = View.VISIBLE
                    passwordEditText.setSelection(oldPosition)
                }
            }
            showRepeatPasswordImageView.setOnClickListener {
                val oldPosition = position
                isRepeatPasswordVisible = true
                bindingPopUp.apply {
                    repeatPasswordEditText.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    showRepeatPasswordImageView.visibility = View.INVISIBLE
                    hideRepeatPasswordImageView.visibility = View.VISIBLE
                    repeatPasswordEditText.setSelection(oldPosition)
                }
            }
            hidePasswordImageView.setOnClickListener {
                val oldPosition = position
                isPasswordVisible = false
                bindingPopUp.passwordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                bindingPopUp.showPasswordImageView.visibility = View.VISIBLE
                bindingPopUp.hidePasswordImageView.visibility = View.INVISIBLE
                bindingPopUp.passwordEditText.setSelection(oldPosition)
            }
            hideRepeatPasswordImageView.setOnClickListener {
                val oldPosition = position
                isRepeatPasswordVisible = false
                bindingPopUp.repeatPasswordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                bindingPopUp.showRepeatPasswordImageView.visibility = View.VISIBLE
                bindingPopUp.hideRepeatPasswordImageView.visibility = View.INVISIBLE
                bindingPopUp.repeatPasswordEditText.setSelection(oldPosition)
            }
        }
    }

    private fun setErrorState(errorMessage: String) {
        with(bindingPopUp.passwordEditText) {
            text?.clear()
            clearFocus()
            hideKeyboard()
            background = ContextCompat.getDrawable(
                dialog.context, R.drawable.shape_wrong_password
            )
            hint = ""
        }
        with(bindingPopUp.repeatPasswordEditText) {
            text?.clear()
            clearFocus()
            background = ContextCompat.getDrawable(
                dialog.context, R.drawable.shape_wrong_password
            )
            hint = ""
        }
        with(bindingPopUp.errorText) {
            text = errorMessage
            visibility = View.VISIBLE
        }
    }

    private fun clearErrorState() {
        bindingPopUp.passwordEditText.background = ContextCompat.getDrawable(
            dialog.context, R.drawable.shape_password_field
        )
        bindingPopUp.repeatPasswordEditText.background = ContextCompat.getDrawable(
            dialog.context, R.drawable.shape_password_field
        )
        bindingPopUp.errorText.visibility = View.INVISIBLE
    }

    private fun isErrorState(): Boolean {
        return bindingPopUp.errorText.visibility == View.VISIBLE
    }

}
