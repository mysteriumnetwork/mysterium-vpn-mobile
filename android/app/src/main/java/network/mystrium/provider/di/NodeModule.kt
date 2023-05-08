package network.mystrium.provider.di

import network.mystrium.node.MobileNodeFactory
import org.koin.dsl.module

val nodeModule = module {
    single {
        MobileNodeFactory.make()
    }
}
