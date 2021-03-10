package updated.mysterium.vpn

import android.app.Application
import io.intercom.android.sdk.Intercom
import network.mysterium.ui.Countries
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import updated.mysterium.vpn.di.Modules

class App : Application() {

    private companion object {
        const val API_KEY = "android_sdk-e480f3fce4f2572742b13c282c453171c1715516"
        const val APP_ID = "h7hlm9on"
    }

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
        Intercom.initialize(this, API_KEY, APP_ID)
    }
}
