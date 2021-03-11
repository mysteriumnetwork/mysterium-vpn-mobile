package updated.mysterium.vpn

import android.app.Application
import io.intercom.android.sdk.Intercom
import network.mysterium.ui.Countries
import network.mysterium.vpn.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import updated.mysterium.vpn.di.Modules

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Countries.loadBitmaps()
        setupIntercom()
        startKoin {
            androidContext(this@App)
            modules(Modules.main)
        }
    }

    private fun setupIntercom() {
        Intercom.initialize(
            this,
            BuildConfig.INTERCOM_API_KEY,
            BuildConfig.INTERCOM_APP_ID
        )
    }
}
