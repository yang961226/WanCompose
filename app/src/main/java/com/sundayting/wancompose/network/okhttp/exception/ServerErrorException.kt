package com.sundayting.wancompose.network.okhttp.exception

import okhttp3.ResponseBody
import okio.IOException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 服务器错误导致的异常（非200状态码）
 * @param code http状态码
 * @param errorBody http报错体（内容可能较大，慎重调取）
 */
class ServerErrorException(
    val code: Int,
    val errorBody: ResponseBody?,
) : IOException()

/**
 * 校验当前异常为[ServerErrorException]
 * @see [ServerErrorException]
 */
@OptIn(ExperimentalContracts::class)
fun Throwable.isServerErrorException(): Boolean {
    contract {
        returns(true) implies (this@isServerErrorException is ServerErrorException)
    }
    return this is ServerErrorException
}


