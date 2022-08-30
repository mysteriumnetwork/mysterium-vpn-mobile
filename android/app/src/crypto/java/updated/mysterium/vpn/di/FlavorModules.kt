package updated.mysterium.vpn.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import updated.mysterium.vpn.ui.top.up.amount.usd.TopUpAmountUsdViewModel
import updated.mysterium.vpn.ui.top.up.card.currency.SelectCountryViewModel
import updated.mysterium.vpn.ui.top.up.card.payment.CardPaymentViewModel
import updated.mysterium.vpn.ui.top.up.card.summary.CardSummaryViewModel
import updated.mysterium.vpn.ui.top.up.crypto.payment.CryptoPaymentViewModel

object FlavorModules {

    val main = module {
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
            SelectCountryViewModel(get())
        }
        viewModel {
            CardPaymentViewModel(get())
        }
    }
}

