package com.sundayting.wancompose.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Ktor {

    val client = HttpClient(Android) {
        expectSuccess = true
        install(Resources)
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