package network.mystrium.provider.di

import network.mystrium.provider.ui.screens.launch.LaunchViewModel
import network.mystrium.provider.ui.screens.settings.SettingsViewModel
import network.mystrium.provider.ui.screens.tac.TACViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModels = module {
    viewModel {
        LaunchViewModel()
    }
    viewModel {
        TACViewModel(node = get())
    }
    viewModel {
        SettingsViewModel()
    }
}
