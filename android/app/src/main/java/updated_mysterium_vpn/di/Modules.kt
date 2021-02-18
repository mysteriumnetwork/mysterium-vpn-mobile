package updated_mysterium_vpn.di

import android.content.Context
import androidx.room.Room
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import updated_mysterium_vpn.database.AppDatabase
import updated_mysterium_vpn.network.provider.usecase.UseCaseProvider
import updated_mysterium_vpn.ui.manual_connect.county.CountrySelectViewModel
import updated_mysterium_vpn.ui.splash.SplashViewModel

object Modules {

    val main = module {
        single { provideDatabase(androidContext()) }
        single { DeferredNode() }
        single { NodeRepository(get()) }
        single { UseCaseProvider(get(), get()) }
        viewModel { CountrySelectViewModel(get()) }
        viewModel { SplashViewModel(get(), get()) }
    }

    private fun provideDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "MYSTERIUM_DATABASE"
    ).build()
}
