package com.sundayting.wancompose.network.okhttp.retrofit

import android.util.Log
import com.sundayting.wancompose.network.okhttp.NResult
import com.sundayting.wancompose.network.okhttp.calldelegate.CallAdapterFactory
import com.sundayting.wancompose.network.okhttp.calldelegate.ResultTransformer
import com.sundayting.wancompose.network.okhttp.exception.FailureReason
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .baseUrl("https://www.wanandroid.com")
            .build()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory {
        return MoshiConverterFactory.create()
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class TimeOut

    @TimeOut
    @Provides
    @Singleton
    fun provideTimeOutSeconds(): Long {
        return 10L
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        @TimeOut timeOut: Long,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor(
                logger = {
                    Log.d("网络请求日志", it)
                }
            ).apply {
                level = BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return CallAdapterFactory.create(
            object : ResultTransformer<Type> {
                override fun onHttpException(t: Throwable): NResult<Type> {
                    return NResult.NFailure(FailureReason("网络错误", t))
                }

                override fun onHttpSuccess(response: Response<Type>): NResult<Type> {
                    return NResult.NSuccess(response)
                }

            }
        )
    }

}