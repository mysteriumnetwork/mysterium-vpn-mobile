package network.mysterium.provider.di

import network.mysterium.node.NodeFactory
import network.mysterium.node.network.NetworkReporter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val nodeModule = module {
    single {
        NodeFactory.make(androidContext())
    }
    factory {
        NetworkReporter(androidContext())
    }
}