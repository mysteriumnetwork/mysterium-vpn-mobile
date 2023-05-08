package network.mystrium.provider

import android.app.Application
import network.mystrium.provider.di.nodeModule
import network.mystrium.provider.di.viewModels
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                nodeModule,
                viewModels
            )
        }
    }
}
