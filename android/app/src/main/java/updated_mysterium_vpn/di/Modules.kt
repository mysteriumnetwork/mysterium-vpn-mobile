package updated_mysterium_vpn.di

import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.NodeRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import updated_mysterium_vpn.network.provider.usecase.UseCaseProvider
import updated_mysterium_vpn.ui.manual_connect.ManualConnectViewModel
import updated_mysterium_vpn.ui.manual_connect.county.CountrySelectViewModel

object Modules {

    val main = module {
        single { DeferredNode() }
        single { NodeRepository(get()) }
        single { UseCaseProvider(get()) }
        viewModel { CountrySelectViewModel(get()) }
        viewModel { ManualConnectViewModel(get()) }
    }
}
