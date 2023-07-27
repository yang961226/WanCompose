package com.sundayting.wancompose.network

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.Cookie
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object KtorModule {

    @Provides
    @Singleton
    fun providerKtorClient(
        cookiesStorage: CookiesStorage,
    ): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            install(Resources)
            install(HttpCookies) {
                storage = cookiesStorage
            }
            install(ContentNegotiation) {
                json(json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })

            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("网络请求日志", message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                host = "www.wanandroid.com"
                url { protocol = URLProtocol.HTTPS }
            }
        }
    }

}

@Module
@InstallIn(SingletonComponent::class)
interface CookiesStorageBinder {

    @Binds
    fun bindCookiesStorage(
        storage: LoginCookieStorage,
    ): CookiesStorage

}

class LoginCookieStorage @Inject constructor(
    @ApplicationContext private val application: Context,
) : CookiesStorage {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

    private val mutex = Mutex()

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie): Unit = mutex.withLock {
        application.dataStore.edit {
            it[stringPreferencesKey(cookie.name)] = cookie.value
        }
    }


    override fun close() {

    }

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        val preferences = application.dataStore.data.firstOrNull() ?: return emptyList()
        return preferences.asMap().map {
            Cookie(
                name = it.key.name,
                value = it.value.toString()
            )
        }
    }

}