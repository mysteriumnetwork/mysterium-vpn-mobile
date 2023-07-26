package network.mysterium.node.di

import com.google.firebase.analytics.FirebaseAnalytics
import network.mysterium.node.analytics.NodeAnalytics
import network.mysterium.node.analytics.NodeAnalyticsImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {

    single<FirebaseAnalytics> { FirebaseAnalytics.getInstance(androidContext()) }

    single<NodeAnalytics> { NodeAnalyticsImpl(get()) }

}
