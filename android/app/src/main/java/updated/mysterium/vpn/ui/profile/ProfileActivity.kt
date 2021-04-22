package updated.mysterium.vpn.ui.profile

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import network.mysterium.AppNotificationManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityProfileBinding
import network.mysterium.vpn.databinding.PopUpDownloadKeyBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.downloads.DownloadsUtil
import updated.mysterium.vpn.common.extensions.isValidPassword
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity

class ProfileActivity : BaseActivity() {

    private companion object {
        const val TAG = "ProfileActivity"
        const val COPY_LABEL = "User identity address"
    }

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by inject()
    private var identityAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
        requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 1)
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        viewModel.getIdentity().observe(this, { result ->
            result.onSuccess { identity ->
                binding.identityValueTextView.text = identity.address
                identityAddress = identity.address
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Implement error handling")
            }
        })
    }

    private fun bindsAction() {
        binding.copyButton.setOnClickListener {
            copyToClipboard()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.downloadButton.setOnClickListener {
            showDownloadKeyPopUp()
        }
    }

    private fun showDownloadKeyPopUp() {
        val bindingPopUp = PopUpDownloadKeyBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.downloadButton.setOnClickListener {
            val passphrase = bindingPopUp.passwordEditText.text.toString()
            if (passphrase.isValidPassword()) {
                downloadKey(passphrase)
                dialog.dismiss()
            } else {
                bindingPopUp.passwordEditText.background = ContextCompat.getDrawable(
                    this, R.drawable.shape_wrong_password
                )
                bindingPopUp.passwordEditText.text?.clear()
                bindingPopUp.passwordEditText.clearFocus()
                bindingPopUp.errorText.visibility = View.VISIBLE
                bindingPopUp.passwordEditText.hint = ""
            }
        }
        bindingPopUp.passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bindingPopUp.passwordEditText.background = ContextCompat.getDrawable(
                    this, R.drawable.shape_password_field
                )
                bindingPopUp.passwordEditText.text?.clear()
                bindingPopUp.passwordEditText.hint = getString(R.string.pop_up_private_key_hint)
                bindingPopUp.errorText.visibility = View.GONE
            }
        }
        bindingPopUp.closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun downloadKey(passphrase: String) {
        viewModel.downloadKey(passphrase).observe(this, { result ->
            result.onSuccess {
                saveFile(it)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        })
    }

    private fun saveFile(bytesFileContent: ByteArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            DownloadsUtil.saveWithContentResolver(bytesFileContent, contentResolver) {
                scanFile(it)
            }
        } else {
            DownloadsUtil.saveDirectlyToDownloads(bytesFileContent)
            showDownloadedNotification()
        }
    }

    private fun scanFile(path: String) {
        MediaScannerConnection.scanFile(this, arrayOf(path), null) { _, _ ->
            showDownloadedNotification()
        }
    }

    private fun showDownloadedNotification() {
        val appNotificationManager = AppNotificationManager(
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        ).apply { init(this@ProfileActivity) }
        appNotificationManager.showDownloadedNotification()
    }

    private fun copyToClipboard() {
        getSystemService(this, ClipboardManager::class.java)?.let { clipManager ->
            val clipData = ClipData.newPlainText(COPY_LABEL, identityAddress)
            clipManager.setPrimaryClip(clipData)
            showToast()
        }
    }

    private fun showToast() {
        Toast.makeText(
            this,
            getString(R.string.profile_copy_to_clipboard),
            Toast.LENGTH_LONG
        ).show()
    }
}
