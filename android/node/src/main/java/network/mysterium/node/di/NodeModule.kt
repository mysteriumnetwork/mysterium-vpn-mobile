package network.mysterium.node.di

import mysterium.Mysterium
import network.mysterium.node.Node
import network.mysterium.node.Storage
import network.mysterium.node.battery.BatteryStatus
import network.mysterium.node.core.NodeContainer
import network.mysterium.node.core.NodeImpl
import network.mysterium.node.core.StorageImpl
import network.mysterium.node.data.NodeServiceDataSource
import network.mysterium.node.data.NodeServiceDataSourceImpl
import network.mysterium.node.network.NetworkReporter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val nodeModule = module {

    single { BatteryStatus(androidContext()) }

    single<Storage> { StorageImpl(androidContext()) }

    single {
        NetworkReporter(androidContext())
    }

    single<Node> { NodeImpl(get(), get(), get()) }

    single<NodeContainer> {
        try {
            NodeContainer(
                mobileNode = Mysterium.newNode(
                    androidContext().filesDir.canonicalPath,
                    Mysterium.defaultProviderNodeOptions()
                )
            )
        } catch (e: Throwable) {
            NodeContainer(mobileNode = null, e)
        }
    }

    single<NodeServiceDataSource> {
        NodeServiceDataSourceImpl(
            nodeContainer = get(),
            storage = get(),
            networkReporter = get(),
            analytics = get(),
        )
    }
}
