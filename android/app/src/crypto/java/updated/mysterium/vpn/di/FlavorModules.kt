package updated.mysterium.vpn.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import updated.mysterium.vpn.core.NodeRepository
import updated.mysterium.vpn.database.preferences.SharedPreferencesManager
import updated.mysterium.vpn.network.provider.usecase.UseCaseProvider
import updated.mysterium.vpn.ui.top.up.amount.usd.TopUpAmountUsdViewModel
import updated.mysterium.vpn.ui.top.up.card.currency.CardCurrencyViewModel
import updated.mysterium.vpn.ui.top.up.card.summary.CardSummaryViewModel
import updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentViewModel

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
        viewModel {
            CryptoPaymentViewModel(get())
        }
        viewModel {
            TopUpAmountUsdViewModel(get())
        }
        viewModel {
            CardSummaryViewModel(get())
        }
        viewModel {
            CardCurrencyViewModel(get())
        }
    }
}

