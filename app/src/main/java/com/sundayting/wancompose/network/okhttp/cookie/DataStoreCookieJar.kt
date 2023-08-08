package com.sundayting.wancompose.network.okhttp.cookie

import android.content.Context
import androidx.datastore.dataStore
import com.sundayting.wancompose.protobuf.CookieProtobuf
import com.sundayting.wancompose.protobuf.CookieProtobuf.CookiesProto.CookieMap
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton

private val Context.cookieJarDataStore by dataStore(
    fileName = "cookie.pd",
    serializer = CookieSerializer
)

/**
 * 基于DataStore实现的CookieJar
 */
@Singleton
class DataStoreCookieJar @Inject constructor(
    @ApplicationContext context: Context,
) : CookieJar {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DataStoreCookieJarEntryPoint {
        fun cookieJar(): DataStoreCookieJar
    }

    companion object {
        fun getInstance(context: Context): DataStoreCookieJar {
            return EntryPointAccessors.fromApplication(
                context,
                DataStoreCookieJarEntryPoint::class.java
            ).cookieJar()
        }

    }

    private val dataStore = context.cookieJarDataStore
    private val mutex = Mutex()

    suspend fun clearExpireCookie(host: String) {
        mutex.withLock {
            val curTime = System.currentTimeMillis()
            val cookies =
                dataStore.data.map { it.cookiesForHostMap[host] }.firstOrNull() ?: return
            dataStore.updateData { cookiesProto ->
                cookiesProto.toBuilder()
                    .putCookiesForHost(host, CookieMap.newBuilder()
                        .apply {
                            cookies.cookiesMap
                                //过滤掉所有过期的
                                .filter { it.value.expiresAt >= curTime }
                                .forEach {
                                    putCookies(it.key, it.value)
                                }
                        }
                        .build())
                    .build()
            }
        }
    }

    suspend fun clearExpireCookie(url: HttpUrl) {
        clearExpireCookie(url.host)
    }

    private fun CookieProtobuf.CookiesProto.Cookie.toOkhttpCookie(
        name: String,
    ): Cookie {
        return Cookie.Builder()
            .name(name)
            .value(value)
            .expiresAt(expiresAt)
            .apply {
                if (hostOnly) {
                    hostOnlyDomain(domain)
                } else {
                    domain(domain)
                }
            }
            .path(path)
            .apply {
                if (secure) {
                    secure()
                }
                if (hostOnly) {
                    httpOnly()
                }
            }
            .build()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> = runBlocking {
        mutex.withLock {
            val curTimeMills = System.currentTimeMillis()
            dataStore.data.map {
                it.cookiesForHostMap[url.host]?.cookiesMap.orEmpty()
                    .filter { entry ->
                        entry.value.expiresAt >= curTimeMills
                    }.map { entry ->
                        entry.value.toOkhttpCookie(entry.key)
                    }
            }.first().toList()
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        runBlocking {
            mutex.withLock {
                dataStore.updateData { curCookie ->
                    val existCookie =
                        dataStore.data.map { it.cookiesForHostMap[url.host]?.cookiesMap }
                            .firstOrNull()
                            .orEmpty().toList()
                    curCookie.toBuilder()
                        .putCookiesForHost(url.host, CookieMap.newBuilder()
                            .apply {
                                //补全旧的
                                existCookie.forEach {
                                    putCookies(it.first, it.second)
                                }
                                //添加新的
                                (cookies).forEach { okhttpCookie ->
                                    putCookies(
                                        okhttpCookie.name,
                                        CookieProtobuf.CookiesProto.Cookie.newBuilder()
                                            .setValue(okhttpCookie.value)
                                            .setExpiresAt(okhttpCookie.expiresAt)
                                            .setDomain(okhttpCookie.domain)
                                            .setPath(okhttpCookie.path)
                                            .setSecure(okhttpCookie.secure)
                                            .setHostOnly(okhttpCookie.hostOnly)
                                            .setHostOnly(okhttpCookie.hostOnly)
                                            .build()
                                    )
                                }
                            }
                            .build())
                        .build()
                }
            }
        }
    }
}