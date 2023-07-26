package com.sundayting.wancompose.network

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

sealed class NetResult<T> {

    data class Success<T>(val data: T) : NetResult<T>()
    class Error(val ex: Throwable) : NetResult<Nothing>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(ex: Throwable) = Error(ex)
    }

}

class NetResultResponseConverterFactory : Converter.Factory {
    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == NetResult::class) {
            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(response: HttpResponse): Any {
                    return try {
                        val result =
                            response.body<WanNetResult<*>>(typeData.typeArgs.first().typeInfo)
                        if (result.errorCode != 0) {
                            return NetResult.error(Exception())
                        } else {
                            NetResult.success(result)
                        }
                    } catch (ex: Throwable) {
                        NetResult.error(ex)
                    }
                }
            }
        }
        return null
    }

}