package updated_mysterium_vpn.ui.manual_connect

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import network.mysterium.service.core.MysteriumAndroidCoreService
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityManualConnectBinding
import org.koin.android.ext.android.inject
import updated_mysterium_vpn.ui.manual_connect.county.CountrySelectFragment

class ManualConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualConnectBinding
    private val viewModel: ManualConnectViewModel by inject()
    private val deferredMysteriumCoreService = CompletableDeferred<MysteriumCoreService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindMysteriumService()
        viewModel.startDeferredNode(deferredMysteriumCoreService)
        showCountrySelectScreen()
    }

    private fun showCountrySelectScreen() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CountrySelectFragment())
                .commit()
    }

    private fun bindMysteriumService() {
        Intent(this, MysteriumAndroidCoreService::class.java).also { intent ->
            bindService(
                    intent,
                    object : ServiceConnection {

                        override fun onServiceDisconnected(name: ComponentName?) {
                            Log.i(TAG, "Service disconnected")
                        }

                        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                            Log.i(TAG, "Service connected")
                            deferredMysteriumCoreService.complete(service as MysteriumCoreService)
                        }
                    },
                    Context.BIND_AUTO_CREATE
            )
        }
    }

    companion object {

        private const val TAG = "ManualConnectActivity"
    }
}
