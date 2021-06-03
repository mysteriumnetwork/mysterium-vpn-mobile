package updated.mysterium.vpn.ui.profile

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityProfileBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.downloads.DownloadsUtil
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.pop.up.PopUpDownloadKey

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
            navigateToConnectionOrHome()
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.downloadButton.setOnClickListener {
            showDownloadKeyPopUp()
        }
    }

    private fun showDownloadKeyPopUp() {
        val popUpDownloadKey = PopUpDownloadKey(layoutInflater)
        val dialog = createPopUp(popUpDownloadKey.bindingPopUp.root, true)
        popUpDownloadKey.apply {
            setDialog(dialog)
            downloadAction {
                downloadKey(it)
            }
            setUp()
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
