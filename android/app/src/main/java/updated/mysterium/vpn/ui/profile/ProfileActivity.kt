package updated.mysterium.vpn.ui.profile

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import network.mysterium.AppNotificationManager
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityProfileBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.downloads.DownloadsUtil
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.menu.MenuActivity

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
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
        binding.downloadButton.setOnClickListener {
            viewModel.downloadKey().observe(this, { result ->
                result.onSuccess {
                    saveFile(it)
                }
                result.onFailure { throwable ->
                    Log.e(TAG, throwable.localizedMessage ?: throwable.toString())
                }
            })
        }
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
            ).apply { init(this@ProfileActivity) }
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
