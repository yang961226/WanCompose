package com.sundayting.wancompose.network.okhttp.calldelegate

import com.sundayting.wancompose.network.okhttp.NResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CallAdapterFactory private constructor() : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        return when (getRawType(returnType)) {
            Call::class.java -> {
                val callType = getParameterUpperBound(0, returnType as ParameterizedType)
                when (getRawType(callType)) {
                    NResult::class.java -> {
                        when (callType) {
                            is Class<*> -> {
                                WanComposeCallAdapter(Nothing::class.java)
                            }

                            is ParameterizedType -> {
                                val resultType = getParameterUpperBound(0, callType)
                                WanComposeCallAdapter(resultType)
                            }

                            else -> {
                                null
                            }
                        }

                    }

                    else -> null
                }
            }

            else -> null
        }
    }

    companion object {
        fun create(): CallAdapterFactory {
            return CallAdapterFactory()
        }
    }

    class WanComposeCallAdapter constructor(
        private val resultType: Type,
    ) : CallAdapter<Type, Call<NResult<Type>>> {
        override fun responseType() = resultType

        override fun adapt(call: Call<Type>): Call<NResult<Type>> =
            ResponseCallDelegate(call)

    }
}