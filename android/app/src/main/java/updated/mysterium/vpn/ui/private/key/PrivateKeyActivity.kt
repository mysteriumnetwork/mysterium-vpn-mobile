package updated.mysterium.vpn.ui.private.key

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import network.mysterium.AppNotificationManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityPrivateKeyBinding
import network.mysterium.vpn.databinding.PopUpDownloadKeyBinding
import network.mysterium.vpn.databinding.PopUpRetryRegistrationBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.downloads.DownloadsUtil
import updated.mysterium.vpn.common.extensions.isValidPassword
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

class PrivateKeyActivity : BaseActivity() {

    private companion object {
        const val TAG = "PrivateKeyActivity"
    }

    private lateinit var binding: ActivityPrivateKeyBinding
    private val viewModel: PrivateKeyViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindsAction()
    }

    private fun bindsAction() {
        binding.backUpKeyFrame.setOnClickListener {
            showDownloadKeyPopUp()
        }
        binding.backUpLaterFrame.setOnClickListener {
            val intent = Intent(this, TopUpAmountActivity::class.java).apply {
                putExtra(TopUpAmountActivity.TRIAL_MODE_EXTRA_KEY, true)
            }
            startActivity(intent)
        }
        binding.backButton.setOnClickListener {
            finish()
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
                exportIdentity(passphrase)
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
            DownloadsUtil.saveWithDownloadManager(
                bytesFileContent,
                getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            )
        }
    }

    private fun scanFile(path: String) {
        MediaScannerConnection.scanFile(this, arrayOf(path), null) { _, _ ->
            val appNotificationManager = AppNotificationManager(
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            ).apply { init(this@PrivateKeyActivity) }
            appNotificationManager.showDownloadedNotification()
        }
    }

    private fun exportIdentity(passphrase: String) {
        viewModel.exportIdentity(passphrase).observe(this, { result ->
            result.onSuccess {
                val intent = Intent(this, TopUpAmountActivity::class.java).apply {
                    putExtra(TopUpAmountActivity.TRIAL_MODE_EXTRA_KEY, true)
                }
                startActivity(intent)
            }
            result.onFailure {
                Log.i(TAG, "onFailure ${it.localizedMessage}")
                showRegistrationErrorPopUp(passphrase)
            }
        })
    }

    private fun showRegistrationErrorPopUp(passphrase: String) {
        val bindingPopUp = PopUpRetryRegistrationBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.tryAgainButton.setOnClickListener {
            dialog.dismiss()
            exportIdentity(passphrase)
        }
        bindingPopUp.cancelButton.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            dialog.dismiss()
        }
        dialog.show()
    }
}
