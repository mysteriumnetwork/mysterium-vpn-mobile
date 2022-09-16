package updated.mysterium.vpn.ui.pop.up

import android.text.InputFilter
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

    var bindingPopUp = PopUpDownloadKeyBinding.inflate(layoutInflater)
    private var onDownloadAction: ((String) -> Unit)? = null
    private var isPasswordVisible = false
    private var isRepeatPasswordVisible = false
    private var passwordPosition = 0
    private var repeatPasswordPosition = 0
    private lateinit var dialog: AlertDialog
    private val spaceFilter = InputFilter { source, start, end, _, _, _ ->
        source?.subSequence(start, end)?.replace(Regex("[ ]"), "")
    }

    fun downloadAction(onDownloadAction: (String) -> Unit) {
        this.onDownloadAction = onDownloadAction
    }

    fun setDialog(alertDialog: AlertDialog) {
        dialog = alertDialog
    }

    fun setUp() {
        bindingPopUp.apply {
            downloadButton.setOnClickListener {
                onDownloadButtonClick()
            }
            passwordEditText.filters = arrayOf(spaceFilter)
            passwordEditText.apply {
                filters = arrayOf(spaceFilter)
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
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
                doOnTextChanged { _, _, _, _ ->
                    switchPasswordToggle(isPasswordVisible)
                }
                setSelectionChangedListener {
                    passwordPosition = it
                }
            }
            repeatPasswordEditText.filters = arrayOf(spaceFilter)
            repeatPasswordEditText.apply {
                filters = arrayOf(spaceFilter)
                onFocusChangeListener =
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
                doOnTextChanged { _, _, _, _ ->
                    switchRepeatPasswordToggle(isRepeatPasswordVisible)
                }
                setSelectionChangedListener {
                    repeatPasswordPosition = it
                }
            }
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
            showPasswordImageView.setOnClickListener { setPasswordVisibility(true) }
            showRepeatPasswordImageView.setOnClickListener { setRepeatPasswordVisibility(true) }
            hidePasswordImageView.setOnClickListener { setPasswordVisibility(false) }
            hideRepeatPasswordImageView.setOnClickListener { setRepeatPasswordVisibility(false) }
        }
    }

    private fun onDownloadButtonClick() {
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

    private fun setPasswordVisibility(isVisible: Boolean) {
        val oldPosition = passwordPosition
        isPasswordVisible = isVisible
        switchPasswordToggle(isPasswordVisible)
        bindingPopUp.passwordEditText.apply {
            transformationMethod = if (isVisible) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
            setSelection(oldPosition)
        }
    }

    private fun setRepeatPasswordVisibility(isVisible: Boolean) {
        val oldPosition = repeatPasswordPosition
        isRepeatPasswordVisible = isVisible
        switchRepeatPasswordToggle(isRepeatPasswordVisible)
        bindingPopUp.repeatPasswordEditText.apply {
            transformationMethod = if (isVisible) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
            setSelection(oldPosition)
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

    private fun switchPasswordToggle(isVisible: Boolean) {
        bindingPopUp.showPasswordImageView.setVisibility(!isVisible)
        bindingPopUp.hidePasswordImageView.setVisibility(isVisible)
    }

    private fun switchRepeatPasswordToggle(isVisible: Boolean) {
        bindingPopUp.showRepeatPasswordImageView.setVisibility(!isVisible)
        bindingPopUp.hideRepeatPasswordImageView.setVisibility(isVisible)
    }


}
