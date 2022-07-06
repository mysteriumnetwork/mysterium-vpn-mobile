package updated.mysterium.vpn.di

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.top.up.play.billing.amount.usd.PlayBillingAmountUsdViewModel
import updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingDataSource
import updated.mysterium.vpn.ui.top.up.play.billing.summary.PlayBillingSummaryViewModel

object FlavorModules {

    val main = module {
        single {
            Modules.provideDatabase(androidContext())
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
            PlayBillingDataSource(androidApplication())
        }
        viewModel {
            PlayBillingAmountUsdViewModel(get(), get())
        }
        viewModel {
            PlayBillingSummaryViewModel(get(), get())
        }
    }
}

