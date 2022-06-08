package updated.mysterium.vpn.ui.profile

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityProfileBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.downloads.DownloadsUtil
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.pop.up.PopUpDownloadKey

class ProfileActivity : BaseActivity() {

    private companion object {
        const val COPY_LABEL = "User identity address"
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by inject()
    private val appNotificationManager: AppNotificationManager by inject()
    private var identityAddress = ""
    private val storagePermissions = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showDownloadKeyPopUp()
            }
        }
        return
    }

    override fun showConnectionHint() {
        binding.connectionHint.visibility = View.VISIBLE
        baseViewModel.hintShown()
    }

    private fun configure() {
        initToolbar(binding.manualConnectToolbar)
        viewModel.getIdentity().observe(this) { result ->
            result.onSuccess { identity ->
                binding.identityValueTextView.text = identity.address
                identityAddress = identity.address
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                // TODO("Implement error handling")
            }
        }
    }

    private fun bindsAction() {
        binding.copyButton.setOnClickListener {
            copyToClipboard()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            navigateToConnectionIfConnectedOrHome()
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            finish()
        }
        binding.downloadButton.setOnClickListener {
            downloadPrivateKey()
        }
    }

    private fun downloadPrivateKey() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (areStoragePermissionsGranted()) {
                showDownloadKeyPopUp()
            } else if (storagePermissions.any { shouldShowRequestPermissionRationale(it) }) {
                showRequestPermissionRationale()
            } else {
                requestStoragePermission()
            }
        } else {
            showDownloadKeyPopUp()
        }
    }

    private fun showRequestPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle(R.string.profile_storage_permission_rationale_title)
            .setMessage(R.string.profile_storage_permission_rationale_message)
            .setNegativeButton(R.string.profile_storage_permission_rationale_cancel) { dialog, _ ->
                dialog.dismiss()
                requestStoragePermission()
            }
            .setPositiveButton(R.string.profile_storage_permission_rationale_deny) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun areStoragePermissionsGranted(): Boolean {
        return storagePermissions.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        requestPermissions(
            storagePermissions,
            STORAGE_PERMISSION_REQUEST_CODE
        )
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
        viewModel.downloadKey(passphrase).observe(this) { result ->
            result.onSuccess {
                saveFile(it)
            }
            result.onFailure { throwable ->
                Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
            }
        }
    }

    private fun saveFile(bytesFileContent: ByteArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            DownloadsUtil.saveWithContentResolver(bytesFileContent, contentResolver) {
                scanFile(it)
            }
        } else {
            DownloadsUtil.saveDirectlyToDownloads(bytesFileContent)
            appNotificationManager.showDownloadedNotification()
        }
    }

    private fun scanFile(path: String) {
        MediaScannerConnection.scanFile(this, arrayOf(path), null) { _, _ ->
            appNotificationManager.showDownloadedNotification()
        }
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
