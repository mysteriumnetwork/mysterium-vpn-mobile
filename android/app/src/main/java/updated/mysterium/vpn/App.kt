package updated.mysterium.vpn

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CompletableDeferred
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import updated.mysterium.vpn.common.countries.Countries
import updated.mysterium.vpn.common.localisation.LocaleHelper.onAttach
import updated.mysterium.vpn.core.MysteriumAndroidCoreService
import updated.mysterium.vpn.core.MysteriumCoreService
import updated.mysterium.vpn.di.Modules
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel

class App : Application(), LifecycleObserver {

    companion object {
        private const val TAG = "App"

        fun getInstance(context: Context) = context.applicationContext as App
    }

    val deferredMysteriumCoreService = CompletableDeferred<MysteriumCoreService>()
    private val allNodesViewModel: AllNodesViewModel by inject()
    private val exchangeRateViewModel: ExchangeRateViewModel by inject()

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        Countries.loadBitmaps()
        startKoin {
            androidContext(this@App)
            modules(Modules.main)
        }
        bindMysteriumService()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // App goes to foreground, start fetching periodical works
        allNodesViewModel.launchProposalsPeriodically()
        exchangeRateViewModel.launchPeriodicallyExchangeRate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackgroung() {
        // App goes to background, stop fetching periodical works
        allNodesViewModel.stopPeriodicalProposalFetch()
        exchangeRateViewModel.stopPeriodicallyExchangeRate()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(onAttach(base ?: applicationContext))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        onAttach(this)
        super.onConfigurationChanged(newConfig)
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
