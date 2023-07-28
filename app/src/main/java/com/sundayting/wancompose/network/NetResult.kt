package com.sundayting.wancompose.network

import android.app.Application
import android.content.Context
import com.sundayting.wancompose.R
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class NetResult<T> {

    data class Success<T>(val body: T) : NetResult<T>()
    class Error(val ex: WanNetError) : NetResult<Nothing>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(ex: WanNetError) = Error(ex)
    }

}

@OptIn(ExperimentalContracts::class)
fun <T> NetResult<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is NetResult.Success)
        returns(false) implies (this@isSuccess is NetResult.Error)
    }
    return this is NetResult.Success
}

class WanNetError(val msg: String, override val cause: Throwable? = null) : Exception()

@Singleton
class NetResultResponseConverterFactory @Inject constructor(
    @ApplicationContext context: Context,
) : Converter.Factory {

    private val commonNetErrorString = context.getString(R.string.net_error)

    private val mineRepository by lazy {
        MineRepository.getInstance(context as Application)
    }

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
                            if (result.errorCode == -1001) {
                                mineRepository.clearLoginUser()
                            }
                            return NetResult.error(WanNetError(result.errorMsg))
                        } else {
                            NetResult.success(result)
                        }
                    } catch (ex: Throwable) {
                        NetResult.error(WanNetError(commonNetErrorString, ex))
                    }
                }
            }
        }
        return null
    }

}