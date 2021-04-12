package updated.mysterium.vpn.ui.create.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityCreateAccountBinding
import network.mysterium.vpn.databinding.PopUpAccountPasswordBinding
import network.mysterium.vpn.databinding.PopUpImportAccountBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.prepare.top.up.PrepareTopUpActivity
import updated.mysterium.vpn.ui.private.key.PrivateKeyActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class CreateAccountActivity : BaseActivity() {

    private companion object {
        const val MIME_TYPE_JSON = "application/json"
        const val KEY_REQUEST_CODE = 0
        const val TAG = "CreateAccountActivity"
    }

    private lateinit var binding: ActivityCreateAccountBinding
    private val viewModel: CreateAccountViewModel by inject()
    private var privateKeyJson: String? = null
    private lateinit var bindingPasswordPopUp: PopUpAccountPasswordBinding
    private lateinit var dialogPasswordPopup: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            startActivity(Intent(this, PrivateKeyActivity::class.java))
        }
        binding.importAccountFrame.setOnClickListener {
            showUploadFilePopUp()
        }
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

    private fun uploadKey() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = MIME_TYPE_JSON
        startActivityForResult(intent, KEY_REQUEST_CODE)
    }

    private fun handleSelectedFile(filePathName: Uri) {
        val br = BufferedReader(InputStreamReader(contentResolver.openInputStream(filePathName)))
        privateKeyJson = br.readLine()
        br.close()
        showPasswordPopUp()
    }

    private fun showPasswordPopUp() {
        bindingPasswordPopUp = PopUpAccountPasswordBinding.inflate(layoutInflater)
        dialogPasswordPopup = createPopUp(bindingPasswordPopUp.root, true)
        bindingPasswordPopUp.applyButton.setOnClickListener {
            privateKeyJson?.let {
                importAccount(it, bindingPasswordPopUp.passwordEditText.text.toString())
            }
        }
        bindingPasswordPopUp.closeButton.setOnClickListener {
            dialogPasswordPopup.dismiss()
        }
        bindingPasswordPopUp.passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bindingPasswordPopUp.passwordEditText.background = ContextCompat.getDrawable(
                    this, R.drawable.shape_password_field
                )
                bindingPasswordPopUp.passwordEditText.text?.clear()
                bindingPasswordPopUp.errorText.visibility = View.GONE
                bindingPasswordPopUp.passwordEditText.hint = getString(R.string.pop_up_password_account_hint)
            }
        }
        dialogPasswordPopup.show()
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
            val intent = Intent(this, PrepareTopUpActivity::class.java)
            startActivity(intent)
        })
    }

    private fun showPasswordWrongState() {
        bindingPasswordPopUp.passwordEditText.background = ContextCompat.getDrawable(
            this, R.drawable.shape_wrong_password
        )
        bindingPasswordPopUp.passwordEditText.text?.clear()
        bindingPasswordPopUp.passwordEditText.clearFocus()
        bindingPasswordPopUp.passwordEditText.hint = ""
        bindingPasswordPopUp.errorText.visibility = View.VISIBLE
    }
}
