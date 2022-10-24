package updated.mysterium.vpn.ui.create.account

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCreateAccountBinding
import network.mysterium.vpn.databinding.PopUpAccountPasswordBinding
import network.mysterium.vpn.databinding.PopUpImportAccountBinding
import network.mysterium.vpn.databinding.PopUpRetryRegistrationBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.App
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.common.extensions.hideKeyboard
import updated.mysterium.vpn.common.extensions.setSelectionChangedListener
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.base.RegistrationViewModel
import updated.mysterium.vpn.ui.pop.up.PopUpSmthWentWrong
import updated.mysterium.vpn.ui.prepare.top.up.PrepareTopUpActivity
import updated.mysterium.vpn.ui.private.key.PrivateKeyActivity
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

class CreateAccountActivity : BaseActivity() {

    private companion object {
        const val MIME_TYPE_JSON = "application/json"
        const val MIME_TYPE_BINARY_FILE = "application/octet-stream"
        const val KEY_REQUEST_CODE = 0
    }

    private val viewModel: CreateAccountViewModel by inject()
    private val balanceViewModel: BalanceViewModel by inject()
    private val registrationViewModel: RegistrationViewModel by inject()
    private var privateKeyJson: String? = null
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var bindingPasswordPopUp: PopUpAccountPasswordBinding
    private lateinit var dialogPasswordPopup: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeViewModel()
        bindsAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == KEY_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let {
                handleSelectedFile(it)
            }
        }
    }

    private fun bindsAction() {
        binding.createNewAccountFrame.setOnClickListener {
            viewModel.createNewAccount()
        }
        binding.importAccountFrame.setOnClickListener {
            showUploadFilePopUp()
        }
    }

    private fun subscribeViewModel() {
        viewModel.navigateForward.observe(this) {
            navigateToPrivateKey()
        }
        viewModel.registrationError.observe(this) {
            binding.createNewAccountFrame.isClickable = true
            binding.importAccountFrame.isClickable = true
            showRegistrationErrorPopUp()
        }
        registrationViewModel.identityRegistrationResult.observe(this) { isRegistered ->
            binding.loader.visibility = View.INVISIBLE
            if (isRegistered) {
                navigateToConnectionIfConnectedOrHome(isBackTransition = false)
                finish()
            } else {
                navigateToTopUp()
            }
        }
        registrationViewModel.identityRegistrationError.observe(this) {
            binding.loader.visibility = View.INVISIBLE
            detailedErrorPopUp {
                registrationViewModel.tryRegisterIdentity()
            }
        }
    }

    private fun uploadKey() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(MIME_TYPE_BINARY_FILE))
        } else {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(MIME_TYPE_JSON))
        }
        startActivityForResult(intent, KEY_REQUEST_CODE)
    }

    private fun handleSelectedFile(filePathName: Uri) {
        try {
            val br =
                BufferedReader(InputStreamReader(contentResolver.openInputStream(filePathName)))
            privateKeyJson = br.readLine()
            br.close()
        } catch (exception: FileNotFoundException) {
            Log.e(TAG, "Handle Selected File failed with $exception")
        } catch (exception: Exception) {
            Log.e(TAG, "Handle Selected File failed with $exception")
        }
        showPasswordPopUp()
    }

    private fun importAccount(privateKey: String, passphrase: String) {
        viewModel.importAccount(privateKey, passphrase).observe(this) { result ->
            result.onSuccess { identityAddress ->
                dialogPasswordPopup.dismiss()
                upgradeIdentityIfNeeded(identityAddress)
            }
            result.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
                showPasswordWrongState()
            }
        }
    }

    private fun upgradeIdentityIfNeeded(identityAddress: String) {
        viewModel.upgradeIdentityIfNeeded(identityAddress).observe(this) { result ->
            result.onSuccess {
                binding.loader.visibility = View.VISIBLE
                applyNewIdentity(identityAddress)
            }
            result.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
                showSmthWentWrongPopUp {
                    upgradeIdentityIfNeeded(identityAddress)
                }
            }
        }
    }

    private fun applyNewIdentity(newIdentityAddress: String) {
        binding.loader.visibility = View.VISIBLE

        viewModel.applyNewIdentity(newIdentityAddress).observe(this) {
            it.onSuccess { identity ->
                val deferredMysteriumCoreService =
                    App.getInstance(this).deferredMysteriumCoreService
                balanceViewModel.initDeferredNode(deferredMysteriumCoreService)
                viewModel.accountCreated(false)
                registrationViewModel.tryRegisterIdentity(identity)
            }

            it.onFailure {
                binding.loader.visibility = View.GONE
                detailedErrorPopUp {
                    applyNewIdentity(newIdentityAddress)
                }
            }
        }
    }

    private fun showPasswordWrongState() {
        bindingPasswordPopUp.applyLoader.visibility = View.GONE
        bindingPasswordPopUp.applyButton.visibility = View.VISIBLE
        bindingPasswordPopUp.passwordEditText.text?.clear()
        bindingPasswordPopUp.passwordEditText.clearFocus()
        bindingPasswordPopUp.passwordEditText.background = ContextCompat.getDrawable(
            this, R.drawable.shape_wrong_password
        )
        bindingPasswordPopUp.passwordEditText.hint = ""
        bindingPasswordPopUp.errorText.visibility = View.VISIBLE
    }

    private fun showSmthWentWrongPopUp(retryAction: () -> Unit) {
        val popUpSmthWentWrong = PopUpSmthWentWrong(layoutInflater)
        val dialog = createPopUp(popUpSmthWentWrong.bindingPopUp.root, true)
        popUpSmthWentWrong.apply {
            setDialog(dialog)
            retryAction {
                retryAction.invoke()
            }
            setUp()
        }
        dialog.show()
    }

    private fun showRegistrationErrorPopUp() {
        val bindingPopUp = PopUpRetryRegistrationBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.tryAgainButton.setOnClickListener {
            dialog.dismiss()
            viewModel.createNewAccount()
        }
        bindingPopUp.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showUploadFilePopUp() {
        val bindingPopUp = PopUpImportAccountBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.uploadButton.setOnClickListener {
            uploadKey()
            dialog.dismiss()
        }
        bindingPopUp.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        bindingPopUp.closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showPasswordPopUp() {
        bindingPasswordPopUp = PopUpAccountPasswordBinding.inflate(layoutInflater)
        dialogPasswordPopup = createPopUp(bindingPasswordPopUp.root, true)
        bindingPasswordPopUp.apply {
            applyButton.setOnClickListener {
                passwordEditText.clearFocus()
                passwordEditText.hideKeyboard()
                privateKeyJson?.let {
                    importAccount(it, bindingPasswordPopUp.passwordEditText.text.toString())
                    bindingPasswordPopUp.applyLoader.visibility = View.VISIBLE
                    bindingPasswordPopUp.applyButton.visibility = View.GONE
                }
            }
            closeButton.setOnClickListener {
                dialogPasswordPopup.dismiss()
            }
            passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    bindingPasswordPopUp.passwordEditText.hint = getString(
                        R.string.pop_up_password_account_hint
                    )
                    clearErrorState(bindingPasswordPopUp)
                }
            }
            passwordEditText.doOnTextChanged { _, _, _, _ ->
                clearErrorState(bindingPasswordPopUp)
            }
            var position = 0
            bindingPasswordPopUp.passwordEditText.setSelectionChangedListener {
                position = it
            }
            showPasswordImageView.setOnClickListener {
                val oldPosition = position
                bindingPasswordPopUp.passwordEditText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                bindingPasswordPopUp.showPasswordImageView.visibility = View.INVISIBLE
                bindingPasswordPopUp.hidePasswordImageView.visibility = View.VISIBLE
                bindingPasswordPopUp.passwordEditText.setSelection(oldPosition)
            }
            hidePasswordImageView.setOnClickListener {
                val oldPosition = position
                bindingPasswordPopUp.passwordEditText.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                bindingPasswordPopUp.showPasswordImageView.visibility = View.VISIBLE
                bindingPasswordPopUp.hidePasswordImageView.visibility = View.INVISIBLE
                bindingPasswordPopUp.passwordEditText.setSelection(oldPosition)
            }
        }
        dialogPasswordPopup.show()
    }

    private fun clearErrorState(bindingPopUp: PopUpAccountPasswordBinding) {
        bindingPopUp.passwordEditText.background = ContextCompat.getDrawable(
            this, R.drawable.shape_password_field
        )
        bindingPopUp.errorText.visibility = View.GONE
    }

    private fun navigateToPrivateKey() {
        startActivity(Intent(this, PrivateKeyActivity::class.java))
    }

    private fun navigateToTopUp() {
        startActivity(Intent(this, PrepareTopUpActivity::class.java))
    }
}
