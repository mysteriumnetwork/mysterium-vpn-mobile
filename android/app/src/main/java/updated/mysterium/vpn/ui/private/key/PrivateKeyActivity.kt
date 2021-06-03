package updated.mysterium.vpn.ui.private.key

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityPrivateKeyBinding
import network.mysterium.vpn.databinding.PopUpDownloadKeyBinding
import network.mysterium.vpn.databinding.PopUpRetryRegistrationBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.downloads.DownloadsUtil
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.notification.Notifications.Companion.PERMISSION_REQUEST_EXT_STORAGE
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.pop.up.PopUpDownloadKey
import updated.mysterium.vpn.ui.prepare.top.up.PrepareTopUpActivity

class PrivateKeyActivity : BaseActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        showDownloadKeyPopUp()
    }

    private fun bindsAction() {
        binding.backUpKeyFrame.setOnClickListener {
            checkPermissions()
        }
        binding.backUpLaterFrame.setOnClickListener {
            navigateToPrepareTopUp()
        }
    }

    private fun checkPermissions() {
        if (
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_EXT_STORAGE
            )
        } else {
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

    private fun clearErrorState(bindingPopUp: PopUpDownloadKeyBinding) {
        bindingPopUp.passwordEditText.background = ContextCompat.getDrawable(
            this, R.drawable.shape_password_field
        )
        bindingPopUp.errorText.visibility = View.GONE
    }

    private fun downloadKey(passphrase: String) {
        viewModel.downloadKey(passphrase).observe(this, { result ->
            result.onSuccess {
                saveFile(it)
                exportIdentity(passphrase)
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
        ).apply { init(this@PrivateKeyActivity) }
        appNotificationManager.showDownloadedNotification()
    }

    private fun exportIdentity(passphrase: String) {
        viewModel.exportIdentity(passphrase).observe(this, { result ->
            result.onSuccess {
                navigateToPrepareTopUp()
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

    private fun navigateToPrepareTopUp() {
        viewModel.accountCreated()
        val intent = Intent(this, PrepareTopUpActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(PrepareTopUpActivity.IS_NEW_USER_KEY, true)
        }
        startActivity(intent)
    }
}
