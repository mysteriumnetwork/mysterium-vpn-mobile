package updated.mysterium.vpn.ui.splash

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CompletableDeferred
import network.mysterium.service.core.MysteriumAndroidCoreService
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.vpn.databinding.ActivitySplashBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
        private const val VPN_SERVICE_REQUEST = 1
    }

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by inject()
    private val deferredMysteriumCoreService = CompletableDeferred<MysteriumCoreService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindMysteriumService()
        subscribeViewModel()
        ensureVpnServicePermission()
        viewModel.startLoading(deferredMysteriumCoreService)
    }

    private fun subscribeViewModel() {
        viewModel.navigateForward.observe(this, { navigateForward() })
    }

    private fun navigateForward() {
        if (viewModel.isUserAlreadyLogin()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }
    }

    private fun ensureVpnServicePermission() {
        val intent: Intent = VpnService.prepare(this) ?: return
        startActivityForResult(intent, VPN_SERVICE_REQUEST)
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
}
