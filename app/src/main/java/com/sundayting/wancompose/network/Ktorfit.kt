package com.sundayting.wancompose.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object KtorfitModule {

    @Provides
    @Singleton
    fun providerKtorfit(
        httpClient: HttpClient,
    ): Ktorfit {
        return Ktorfit.Builder()
            .converterFactories(NetResultResponseConverterFactory())
            .httpClient(httpClient)
            .build()
    }
}