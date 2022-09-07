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
import updated.mysterium.vpn.common.extensions.setVisibility

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
                switchPasswordToggle(isPasswordVisible)
            }
            repeatPasswordEditText.doOnTextChanged { _, _, _, _ ->
                switchRepeatPasswordToggle(isRepeatPasswordVisible)
            }
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
            var passwordPosition = 0
            var repeatPasswordPosition = 0
            bindingPopUp.passwordEditText.setSelectionChangedListener {
                passwordPosition = it
            }
            bindingPopUp.repeatPasswordEditText.setSelectionChangedListener {
                repeatPasswordPosition = it
            }
            showPasswordImageView.setOnClickListener {
                val oldPosition = passwordPosition
                isPasswordVisible = true
                switchPasswordToggle(isPasswordVisible)
                bindingPopUp.apply {
                    passwordEditText.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    passwordEditText.setSelection(oldPosition)
                }
            }
            showRepeatPasswordImageView.setOnClickListener {
                val oldPosition = repeatPasswordPosition
                isRepeatPasswordVisible = true
                switchRepeatPasswordToggle(isRepeatPasswordVisible)
                bindingPopUp.apply {
                    repeatPasswordEditText.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    repeatPasswordEditText.setSelection(oldPosition)
                }
            }
            hidePasswordImageView.setOnClickListener {
                val oldPosition = passwordPosition
                isPasswordVisible = false
                switchPasswordToggle(isPasswordVisible)
                bindingPopUp.passwordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                bindingPopUp.passwordEditText.setSelection(oldPosition)
            }
            hideRepeatPasswordImageView.setOnClickListener {
                val oldPosition = repeatPasswordPosition
                isRepeatPasswordVisible = false
                switchRepeatPasswordToggle(isRepeatPasswordVisible)
                bindingPopUp.repeatPasswordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
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
        with(bindingPopUp.passwordHelperText) {
            visibility = View.INVISIBLE
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
        bindingPopUp.passwordHelperText.visibility = View.VISIBLE
    }

    private fun isErrorState(): Boolean {
        return bindingPopUp.errorText.visibility == View.VISIBLE
    }

    private fun switchPasswordToggle(isPasswordVisible: Boolean) {
        bindingPopUp.showPasswordImageView.setVisibility(!isPasswordVisible)
        bindingPopUp.hidePasswordImageView.setVisibility(isPasswordVisible)
    }

    private fun switchRepeatPasswordToggle(isRepeatPasswordVisible: Boolean) {
        bindingPopUp.showRepeatPasswordImageView.setVisibility(!isRepeatPasswordVisible)
        bindingPopUp.hideRepeatPasswordImageView.setVisibility(isRepeatPasswordVisible)
    }


}
