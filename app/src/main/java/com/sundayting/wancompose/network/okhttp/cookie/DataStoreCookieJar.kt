package com.sundayting.wancompose.network.okhttp.cookie

import android.content.Context
import androidx.datastore.dataStore
import com.sundayting.wancompose.protobuf.CookieProtobuf
import com.sundayting.wancompose.protobuf.CookieProtobuf.CookiesProto.CookieMap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
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

    private val dataStore = context.cookieJarDataStore

    override fun loadForRequest(url: HttpUrl): List<Cookie> = runBlocking {
        dataStore.data.map {
            it.cookiesForHostMap[url.host]?.cookiesMap.orEmpty().map { entry ->
                val cookie = entry.value
                Cookie.Builder()
                    .name(entry.key)
                    .value(cookie.value)
                    .expiresAt(cookie.expiresAt)
                    .apply {
                        if (cookie.hostOnly) {
                            hostOnlyDomain(cookie.domain)
                        } else {
                            domain(cookie.domain)
                        }
                    }
                    .path(cookie.path)
                    .apply {
                        if (cookie.secure) {
                            secure()
                        }
                        if (cookie.hostOnly) {
                            httpOnly()
                        }
                    }
                    .build()
            }
        }.first().toList()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        runBlocking {
            dataStore.updateData {
                it.toBuilder()
                    .putCookiesForHost(url.host, CookieMap.newBuilder()
                        .apply {
                            cookies.forEach { okhttpCookie ->
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