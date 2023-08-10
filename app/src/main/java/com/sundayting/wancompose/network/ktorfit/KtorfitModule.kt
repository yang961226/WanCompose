package com.sundayting.wancompose.network.ktorfit

import android.util.Log
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitNeedLoginAgain
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.network.NResult
import com.sundayting.wancompose.network.WanError
import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.network.okhttp.cookie.DataStoreCookieJar
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.HttpResponse
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
        converter: Converter.Factory,
    ): Ktorfit {
        return Ktorfit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .converterFactories(converter)
            .httpClient(httpClient)
            .build()
    }

    @Provides
    fun provideConverterFactory(): Converter.Factory {
        return object : Converter.Factory {

            override fun suspendResponseConverter(
                typeData: TypeData,
                ktorfit: Ktorfit,
            ): Converter.SuspendResponseConverter<HttpResponse, *>? {
                if (typeData.typeInfo.type == NResult::class) {
                    return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                        override suspend fun convert(response: HttpResponse): Any {
                            return try {
                                val body: Any = response.body(typeData.typeArgs.first().typeInfo)
                                if (body is WanNResult<*>) {
                                    if (body.errorCode != 0) {
                                        if (body.errorCode == -1001) {
                                            EventManager.emitNeedLoginAgain()
                                        }
                                        EventManager.emitToast(body.errorMsg)
                                        NResult.Error(WanError(body.errorCode, body.errorMsg))
                                    } else {
                                        NResult.Success(body)
                                    }
                                } else {
                                    NResult.Success(body)
                                }
                            } catch (ex: Throwable) {
                                EventManager.emitToast("发生未知异常:[${ex::class.simpleName}]，请联系开发者")
                                NResult.Error(ex)
                            }
                        }
                    }
                }

                return null
            }
        }
    }

    @Provides
    fun provideHttpClient(
        cookiesStorage: DataStoreCookieJar,
    ): HttpClient {
        return HttpClient(Android) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("网络请求日志", message)
                    }
                }
                level = LogLevel.BODY
            }
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
//            install(HttpCookies) {
//                storage = cookiesStorage
//            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }
    }

}