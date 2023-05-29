package updated.mysterium.vpn.di

import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.room.Room
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import updated.mysterium.vpn.analytics.AnalyticWrapper
import updated.mysterium.vpn.analytics.mysterium.MysteriumAnalytic
import updated.mysterium.vpn.core.DeferredNode
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.AppDatabase
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.notification.AppNotificationManager
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.AllNodesViewModel
import updated.mysterium.vpn.ui.base.BaseViewModel
import updated.mysterium.vpn.ui.base.RegistrationViewModel
import updated.mysterium.vpn.ui.connection.ConnectionViewModel
import updated.mysterium.vpn.ui.create.account.CreateAccountViewModel
import updated.mysterium.vpn.ui.favourites.FavouritesViewModel
import updated.mysterium.vpn.ui.home.selection.HomeSelectionViewModel
import updated.mysterium.vpn.ui.menu.MenuViewModel
import updated.mysterium.vpn.ui.monitoring.MonitoringViewModel
import updated.mysterium.vpn.ui.nodes.list.FilterViewModel
import updated.mysterium.vpn.ui.onboarding.OnboardingViewModel
import updated.mysterium.vpn.ui.prepare.top.up.PrepareTopUpViewModel
import updated.mysterium.vpn.ui.private.key.PrivateKeyViewModel
import updated.mysterium.vpn.ui.profile.ProfileViewModel
import updated.mysterium.vpn.ui.provider.ProviderViewModel
import updated.mysterium.vpn.ui.report.issue.ReportIssueViewModel
import updated.mysterium.vpn.ui.search.SearchViewModel
import updated.mysterium.vpn.ui.settings.SettingsViewModel
import updated.mysterium.vpn.ui.splash.SplashViewModel
import updated.mysterium.vpn.ui.terms.TermsOfUseViewModel
import updated.mysterium.vpn.ui.top.up.select.country.SelectCountryViewModel
import updated.mysterium.vpn.ui.top.up.summary.SummaryViewModel
import updated.mysterium.vpn.ui.wallet.ExchangeRateViewModel
import updated.mysterium.vpn.ui.wallet.WalletViewModel
import updated.mysterium.vpn.ui.wallet.spendings.SpendingsViewModel
import updated.mysterium.vpn.ui.wallet.top.up.TopUpsListViewModel

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
            SharedPreferencesManager(androidContext())
        }
        single {
            UseCaseProvider(get(), get(), get())
        }
        single {
            BalanceViewModel(get())
        }
        single {
            AllNodesViewModel(get())
        }
        single {
            ConnectionViewModel(get())
        }
        single {
            AnalyticWrapper()
        }
        single {
            FilterViewModel(get())
        }
        single {
            HomeSelectionViewModel(get())
        }
        single {
            BaseViewModel(get())
        }
        single {
            AppNotificationManager(
                androidContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            ).apply { init(androidContext()) }
        }
        single {
            ExchangeRateViewModel(get())
        }
        single {
            MysteriumAnalytic(
                androidContext(),
                get(),
                get()
            )
        }
        single {
            WorkManager.getInstance(androidContext())
        }

        single {
            SplashViewModel(get(), get())
        }
        viewModel {
            SearchViewModel()
        }
        viewModel {
            OnboardingViewModel(get())
        }
        viewModel {
            ProfileViewModel(get())
        }
        viewModel {
            ReportIssueViewModel(get())
        }
        viewModel {
            WalletViewModel(get())
        }
        viewModel {
            MonitoringViewModel(get())
        }
        viewModel {
            SpendingsViewModel(get())
        }
        viewModel {
            TermsOfUseViewModel(get())
        }
        viewModel {
            SettingsViewModel(get())
        }
        viewModel {
            TopUpsListViewModel(get())
        }
        viewModel {
            PrivateKeyViewModel(get())
        }
        viewModel {
            CreateAccountViewModel(get())
        }
        viewModel {
            PrepareTopUpViewModel(get())
        }
        viewModel {
            FavouritesViewModel(get())
        }
        viewModel {
            MenuViewModel(get())
        }
        viewModel {
            RegistrationViewModel(get())
        }
        viewModel {
            SummaryViewModel(get())
        }
        viewModel {
            SelectCountryViewModel(get())
        }
        single {
            ProviderViewModel(get())
        }
    }

    fun provideDatabase(context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "MYSTERIUM_DATABASE"
    ).build()
}
