package network.mysterium.provider.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import network.mysterium.provider.data.api.DeeplinkApi
import network.mysterium.provider.data.datasource.DeeplinkDataSource
import network.mysterium.provider.data.datasource.DeeplinkDataSourceImpl
import network.mysterium.provider.domain.DeeplinkRedirectionInteractor
import network.mysterium.provider.domain.DeeplinkRedirectionInteractorImpl
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.Retrofit

val deeplinkModule = module {

    single<DeeplinkApi> {
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl("http://localhost/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(DeeplinkApi::class.java)
    }

    factory<DeeplinkDataSource> { DeeplinkDataSourceImpl(get()) }

    factory<DeeplinkRedirectionInteractor> { DeeplinkRedirectionInteractorImpl(get()) }

}
