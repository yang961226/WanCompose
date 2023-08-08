package com.sundayting.wancompose.network.okhttp.cookie

import android.content.Context
import androidx.datastore.dataStore
import com.sundayting.wancompose.protobuf.CookieProtobuf
import com.sundayting.wancompose.protobuf.CookieProtobuf.CookiesProto.CookieList
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
            it.cookiesMapMap[url.host]?.cookieList.orEmpty().map { cookieProto ->
                Cookie.Builder()
                    .name(cookieProto.name)
                    .value(cookieProto.value)
                    .expiresAt(cookieProto.expiresAt)
                    .apply {
                        if (cookieProto.hostOnly) {
                            hostOnlyDomain(cookieProto.domain)
                        } else {
                            domain(cookieProto.domain)
                        }
                    }
                    .domain(cookieProto.domain)
                    .path(cookieProto.path)
                    .apply {
                        if (cookieProto.secure) {
                            secure()
                        }
                        if (cookieProto.hostOnly) {
                            httpOnly()
                        }
                    }
                    .build()
            }
        }.first()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        runBlocking {
            dataStore.updateData {
                it.toBuilder()
                    .putCookiesMap(
                        url.host,
                        CookieList.newBuilder()
                            .addAllCookie(
                                cookies.map { okhttpCookie ->
                                    CookieProtobuf.CookiesProto.Cookie.newBuilder()
                                        .setName(okhttpCookie.name)
                                        .setValue(okhttpCookie.value)
                                        .setExpiresAt(okhttpCookie.expiresAt)
                                        .setDomain(okhttpCookie.domain)
                                        .setPath(okhttpCookie.path)
                                        .setSecure(okhttpCookie.secure)
                                        .setHostOnly(okhttpCookie.hostOnly)
                                        .setHostOnly(okhttpCookie.hostOnly)
                                        .build()
                                }
                            )
                            .build()
                    )
                    .build()
            }
        }
    }
}