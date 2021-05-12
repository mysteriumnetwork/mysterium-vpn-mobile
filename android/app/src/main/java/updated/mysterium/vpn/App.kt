package updated.mysterium.vpn

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.bugfender.sdk.Bugfender
import io.intercom.android.sdk.Intercom
import kotlinx.coroutines.CompletableDeferred
import network.mysterium.service.core.MysteriumAndroidCoreService
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.ui.Countries
import network.mysterium.vpn.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import updated.mysterium.vpn.di.Modules

class App : Application() {

    companion object {
        private const val TAG = "App"

        fun getInstance(context: Context) = context.applicationContext as App
    }

    val deferredMysteriumCoreService = CompletableDeferred<MysteriumCoreService>()

    override fun onCreate() {
        super.onCreate()
        Countries.loadBitmaps()
        setupIntercom()
        startKoin {
            androidContext(this@App)
            modules(Modules.main)
        }
        setUpBugfender()
        bindMysteriumService()
    }

    private fun setupIntercom() {
        Intercom.initialize(
            this,
            BuildConfig.INTERCOM_API_KEY,
            BuildConfig.INTERCOM_APP_ID
        )
    }

    private fun setUpBugfender() {
        Bugfender.init(this, BuildConfig.BUGFENDER_KEY, BuildConfig.DEBUG)
        Bugfender.enableCrashReporting()
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
