package com.sundayting.wancompose.network.ktorfit

import com.sundayting.wancompose.page.homescreen.article.repo.HomePageService2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object KtorfitModule {


    @Provides
    @Singleton
    fun provideKtorift(
        httpClient: HttpClient,
    ): Ktorfit {
        return Ktorfit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .httpClient(httpClient)
            .build()
    }

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }
    }

    @Provides
    fun provideService(
        ktorfit: Ktorfit,
    ): HomePageService2 {
        return ktorfit.create()
    }

}