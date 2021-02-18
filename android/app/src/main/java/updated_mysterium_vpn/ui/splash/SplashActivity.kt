package updated_mysterium_vpn.ui.splash

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CompletableDeferred
import network.mysterium.service.core.MysteriumAndroidCoreService
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.vpn.databinding.ActivitySplashBinding
import org.koin.android.ext.android.inject
import updated_mysterium_vpn.ui.manual_connect.ManualConnectActivity
import updated_mysterium_vpn.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by inject()
    private val deferredMysteriumCoreService = CompletableDeferred<MysteriumCoreService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindMysteriumService()
        subscribeViewModel()
        viewModel.startLoading(deferredMysteriumCoreService)
    }

    private fun subscribeViewModel() {
        viewModel.navigateToOnboarding.observe(this, { navigateToOnboarding() })
    }

    private fun navigateToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java))
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
        private const val TAG = "SplashActivity"
    }
}
