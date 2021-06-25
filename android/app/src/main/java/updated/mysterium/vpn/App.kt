package updated.mysterium.vpn

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.IBinder
import android.util.Log
import com.bugfender.sdk.Bugfender
import io.intercom.android.sdk.Intercom
import kotlinx.coroutines.CompletableDeferred
import network.mysterium.vpn.BuildConfig
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import updated.mysterium.vpn.analitics.AnalyticEvent
import updated.mysterium.vpn.analitics.AnalyticWrapper
import updated.mysterium.vpn.common.countries.Countries
import updated.mysterium.vpn.common.localisation.LocaleHelper.onAttach
import updated.mysterium.vpn.core.MysteriumAndroidCoreService
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.di.Modules

class App : Application() {

    companion object {
        private const val TAG = "App"

        fun getInstance(context: Context) = context.applicationContext as App
    }

    val deferredMysteriumCoreService = CompletableDeferred<MysteriumCoreService>()
    private val analyticWrapper: AnalyticWrapper by inject()

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
        analyticWrapper.track(AnalyticEvent.LOGIN)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(onAttach(base ?: applicationContext))
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        newConfig?.let {
            onAttach(this)
            super.onConfigurationChanged(newConfig)
        }
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
                        (service as? MysteriumCoreService?)?.let {
                            Log.i(TAG, "Service connected")
                            deferredMysteriumCoreService.complete(it)
                        }
                    }
                },
                Context.BIND_AUTO_CREATE
            )
        }
    }
}
