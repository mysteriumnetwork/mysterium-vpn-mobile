package updated.mysterium.vpn

import android.app.Application
import network.mysterium.ui.Countries
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import updated.mysterium.vpn.di.Modules

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Countries.loadBitmaps()
        startKoin {
            androidContext(this@App)
            modules(Modules.main)
        }
    }
}
