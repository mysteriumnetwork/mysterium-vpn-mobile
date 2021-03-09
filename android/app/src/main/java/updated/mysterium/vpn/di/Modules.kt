package updated.mysterium.vpn.di

import android.content.Context
import androidx.room.Room
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import updated.mysterium.vpn.database.AppDatabase
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.manual.connect.filter.FilterViewModel
import updated.mysterium.vpn.ui.manual.connect.home.HomeViewModel
import updated.mysterium.vpn.ui.manual.connect.search.SearchViewModel
import updated.mysterium.vpn.ui.manual.connect.select.node.all.AllNodesViewModel
import updated.mysterium.vpn.ui.manual.connect.select.node.saved.SavedNodesViewModel
import updated.mysterium.vpn.ui.onboarding.OnboardingViewModel
import updated.mysterium.vpn.ui.profile.ProfileViewModel
import updated.mysterium.vpn.ui.splash.SplashViewModel

object Modules {

    val main = module {
        single {
            provideDatabase(androidContext())
        }
        single {
            DeferredNode()
        }
        single {
            NodeRepository(get())
        }
        single {
            UseCaseProvider(get(), get(), androidContext())
        }
        single {
            BalanceViewModel(get())
        }

        viewModel {
            AllNodesViewModel(get())
        }
        viewModel {
            SplashViewModel(get())
        }
        viewModel {
            HomeViewModel(get())
        }
        viewModel {
            FilterViewModel()
        }
        viewModel {
            SavedNodesViewModel(get())
        }
        viewModel {
            SearchViewModel(get())
        }
        viewModel {
            OnboardingViewModel(get())
        }
        viewModel {
            ProfileViewModel(get())
        }
    }

    private fun provideDatabase(context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "MYSTERIUM_DATABASE"
    ).build()
}
