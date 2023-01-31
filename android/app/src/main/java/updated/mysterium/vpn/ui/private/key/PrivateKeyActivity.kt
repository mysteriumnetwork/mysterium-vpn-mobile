package updated.mysterium.vpn.ui.private.key

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityPrivateKeyBinding
import network.mysterium.vpn.databinding.PopUpRetryRegistrationBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.downloads.DownloadsUtil
import updated.mysterium.vpn.common.extensions.TAG
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.pop.up.PopUpDownloadKey
import updated.mysterium.vpn.ui.pop.up.PopUpSmthWentWrong
import updated.mysterium.vpn.ui.prepare.top.up.PrepareTopUpActivity

class PrivateKeyActivity : BaseActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 0
    }

    private lateinit var binding: ActivityPrivateKeyBinding
    private val viewModel: PrivateKeyViewModel by inject()
    private val appNotificationManager: AppNotificationManager by inject()
    private val storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNextToAccountFrameAvailability()
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

    private fun bindsAction() {
        binding.backUpKeyFrame.setOnClickListener {
            backUpKey()
        }
        binding.nextToAccountFrame.setOnClickListener {
            navigateToPrepareTopUp()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun backUpKey() {
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
                upgradeIdentityIfNeeded(passphrase)
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

    private fun upgradeIdentityIfNeeded(passphrase: String) {
        viewModel.upgradeIdentityIfNeeded().observe(this) { result ->
            result.onSuccess {
                exportIdentity(passphrase)
            }
            result.onFailure { error ->
                Log.e(TAG, error.localizedMessage ?: error.toString())
                showSmthWentWrongPopUp {
                    upgradeIdentityIfNeeded(passphrase)
                }
            }
        }
    }

    private fun exportIdentity(passphrase: String) {
        viewModel.exportIdentity(passphrase).observe(this) { result ->
            setNextToAccountFrameAvailability(result.isSuccess)
            result.onSuccess {
                viewModel.accountCreated()
                navigateToPrepareTopUp()
            }
            result.onFailure {
                Log.i(TAG, "onFailure ${it.localizedMessage}")
                showRegistrationErrorPopUp(passphrase)
            }
        }
    }

    private fun showRegistrationErrorPopUp(passphrase: String) {
        val bindingPopUp = PopUpRetryRegistrationBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.tryAgainButton.setOnClickListener {
            dialog.dismiss()
            exportIdentity(passphrase)
        }
        bindingPopUp.cancelButton.onFocusChangeListener =
            View.OnFocusChangeListener { _, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun navigateToPrepareTopUp() {
        startActivity(Intent(this, PrepareTopUpActivity::class.java))
    }

    private fun setNextToAccountFrameAvailability(isAvailable: Boolean? = null) {
        when (isAvailable) {
            true -> {
                binding.nextToAccountFrame.isEnabled = true
                binding.nextToAccountIcon.isActivated = true
                binding.nextToAccountTitle.alpha = 1f
            }
            false -> {
                binding.nextToAccountFrame.isEnabled = false
                binding.nextToAccountIcon.isActivated = false
                binding.nextToAccountTitle.alpha = 0.5f
            }
            else -> {
                setNextToAccountFrameAvailability(viewModel.isAccountCreated())
            }
        }
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

}
