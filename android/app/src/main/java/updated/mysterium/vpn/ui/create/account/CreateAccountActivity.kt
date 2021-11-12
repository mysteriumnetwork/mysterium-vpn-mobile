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
import updated.mysterium.vpn.common.extensions.hideKeyboard
import updated.mysterium.vpn.common.extensions.setSelectionChangedListener
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.home.selection.HomeSelectionActivity
import updated.mysterium.vpn.ui.prepare.top.up.PrepareTopUpActivity
import updated.mysterium.vpn.ui.private.key.PrivateKeyActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class CreateAccountActivity : BaseActivity() {

    private companion object {
    	const val MIME_TYPE_JSON = "application/json"
	    const val MIME_TYPE_BINARY_FILE = "application/octet-stream"
        const val KEY_REQUEST_CODE = 0
        const val TAG = "CreateAccountActivity"
    }

    private val viewModel: CreateAccountViewModel by inject()
    private val balanceViewModel: BalanceViewModel by inject()
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
            binding.createNewAccountFrame.isClickable = false
            binding.importAccountFrame.isClickable = false
            viewModel.createNewAccount()
        }
        binding.importAccountFrame.setOnClickListener {
            showUploadFilePopUp()
        }
    }

    private fun subscribeViewModel() {
        viewModel.navigateForward.observe(this, {
            viewModel.accountCreated(true)
            val intent = Intent(this, PrivateKeyActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        })
        viewModel.registrationError.observe(this, {
            binding.createNewAccountFrame.isClickable = true
            binding.importAccountFrame.isClickable = true
            showRegistrationErrorPopUp()
        })
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
        val br = BufferedReader(InputStreamReader(contentResolver.openInputStream(filePathName)))
        privateKeyJson = br.readLine()
        br.close()
        showPasswordPopUp()
    }

    private fun importAccount(privateKey: String, passphrase: String) {
        viewModel.importAccount(privateKey, passphrase).observe(this, { result ->
            result.onSuccess {
                dialogPasswordPopup.dismiss()
                applyNewIdentity(it)
            }
            result.onFailure {
                Log.i(TAG, "onFailure $it")
                showPasswordWrongState()
            }
        })
    }

    private fun applyNewIdentity(newIdentityAddress: String) {
        viewModel.applyNewIdentity(newIdentityAddress).observe(this, {
            val deferredMysteriumCoreService = App.getInstance(this).deferredMysteriumCoreService
            balanceViewModel.initDeferredNode(deferredMysteriumCoreService)
            viewModel.accountCreated(false)
            checkFreeRegistration()
        })
    }

    private fun checkFreeRegistration() {
        viewModel.accountCreated(false)
        viewModel.isFreeRegistrationAvailable().observe(this) {
            it.onSuccess { isAvailable ->
                if (isAvailable) {
                    navigateToHome()
                } else {
                    checkBalance()
                }
            }

            it.onFailure {
                navigateToTopUp()
            }
        }
    }

    private fun checkBalance() {
        balanceViewModel.getCurrentBalance().observe(this) {
            it.onSuccess { balance ->
                if (balance > 0.0) {
                    navigateToHome()
                } else {
                    navigateToTopUp()
                }
            }

            it.onFailure {
                navigateToTopUp()
            }
        }
    }

    private fun showPasswordWrongState() {
        bindingPasswordPopUp.passwordEditText.text?.clear()
        bindingPasswordPopUp.passwordEditText.clearFocus()
        bindingPasswordPopUp.passwordEditText.background = ContextCompat.getDrawable(
            this, R.drawable.shape_wrong_password
        )
        bindingPasswordPopUp.passwordEditText.hint = ""
        bindingPasswordPopUp.errorText.visibility = View.VISIBLE
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
                }
            }
            closeButton.setOnClickListener {
                dialogPasswordPopup.dismiss()
            }
            passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    bindingPasswordPopUp.passwordEditText.hint = getString(R.string.pop_up_password_account_hint)
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
                bindingPasswordPopUp.passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                bindingPasswordPopUp.showPasswordImageView.visibility = View.INVISIBLE
                bindingPasswordPopUp.hidePasswordImageView.visibility = View.VISIBLE
                bindingPasswordPopUp.passwordEditText.setSelection(oldPosition)
            }
            hidePasswordImageView.setOnClickListener {
                val oldPosition = position
                bindingPasswordPopUp.passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
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

    private fun navigateToHome() {
        val intent = Intent(this, HomeSelectionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun navigateToTopUp() {
        val intent = Intent(this, PrepareTopUpActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(PrepareTopUpActivity.IS_NEW_USER_KEY, false)
        }
        startActivity(intent)
    }
}
