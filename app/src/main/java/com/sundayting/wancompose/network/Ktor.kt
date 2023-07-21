package com.sundayting.wancompose.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.resources.Resource
import io.ktor.serialization.kotlinx.json.json

object Ktor {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
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

    @Resource("/article")
    class Article {
        @Resource("list")
        class List(val parent: Article = Article()) {

            @Resource("{id}")
            class Id(val parent: List = List(), val id: Long) {
                @Resource("json")
                class Json(val parent: Id)
            }

        }


    }

}