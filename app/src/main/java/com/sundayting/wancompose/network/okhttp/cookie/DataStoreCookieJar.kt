package com.sundayting.wancompose.network.okhttp.cookie

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.sundayting.wancompose.protobuf.CookieProtobuf
import com.sundayting.wancompose.protobuf.CookieProtobuf.CookiesProto.CookieMap
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton


private val Context.cookieJarDataStore by dataStore(
    fileName = "cookie.pd",
    serializer = CookieSerializer
)

object CookieSerializer : Serializer<CookieProtobuf.CookiesProto> {
    override val defaultValue: CookieProtobuf.CookiesProto
        get() = CookieProtobuf.CookiesProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CookieProtobuf.CookiesProto {
        return CookieProtobuf.CookiesProto.parseFrom(input)
    }

    override suspend fun writeTo(t: CookieProtobuf.CookiesProto, output: OutputStream) {
        t.writeTo(output)
    }


}

@Singleton
class DataStoreCookieJar @Inject constructor(
    @ApplicationContext context: Context,
) : CookiesStorage {

    private val dataStore = context.cookieJarDataStore
    private val mutex = Mutex()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DataStoreCookieJarEntryPoint {
        fun cookieJar(): DataStoreCookieJar
    }

    companion object {
        fun getInstance(context: Context): DataStoreCookieJar {
            return EntryPointAccessors.fromApplication(
                context.applicationContext,
                DataStoreCookieJarEntryPoint::class.java
            ).cookieJar()
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        dataStore.updateData { curCookie ->
            curCookie.toBuilder()
                .putCookiesForHost(
                    requestUrl.host,
                    CookieMap.newBuilder()
                        .apply {
                            //添加以前的Cookie
                            curCookie.cookiesForHostMap[requestUrl.host]?.cookiesMap.orEmpty()
                                .forEach {
                                    putCookies(it.key, it.value)
                                }
                            //添加新的Cookie
                            putCookies(
                                cookie.name, CookieProtobuf.CookiesProto.Cookie.newBuilder()
                                    .setValue(cookie.value)
                                    .setExpiresAt(cookie.expires?.timestamp ?: 0)
                                    .setDomain(cookie.domain.orEmpty())
                                    .setPath(cookie.path.orEmpty())
                                    .setSecure(cookie.secure)
                                    .setHttpOnly(cookie.httpOnly)
                                    .setMaxAge(cookie.maxAge ?: 0)
                                    .build()
                            )
                        }
                        .build()
                )
                .build()
        }
    }

    override fun close() {}

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val curTimeMills = System.currentTimeMillis()
        return dataStore.data.map {
            it.cookiesForHostMap[requestUrl.host]?.cookiesMap.orEmpty()
                .filter { entry ->
                    entry.value.expiresAt >= curTimeMills
                }.map { entry ->
                    entry.value.toOkhttpCookie(entry.key)
                }
        }.first().toList()
    }

    suspend fun clearExpireCookie(host: String) {
        mutex.withLock {
            val curTime = System.currentTimeMillis()
            val cookies =
                dataStore.data.map { it.cookiesForHostMap[host] }.firstOrNull() ?: return
            dataStore.updateData { cookiesProto ->
                cookiesProto.toBuilder()
                    .putCookiesForHost(
                        host, CookieMap.newBuilder()
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

    private fun CookieProtobuf.CookiesProto.Cookie.toOkhttpCookie(
        name: String,
    ) = Cookie(
        name = name,
        value = value,
        expires = GMTDate(expiresAt),
        maxAge = maxAge,
        domain = domain,
        path = path,
        secure = secure,
        httpOnly = httpOnly
    )

}