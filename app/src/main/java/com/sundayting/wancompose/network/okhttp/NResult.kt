package com.sundayting.wancompose.network.okhttp

import com.sundayting.wancompose.network.okhttp.NResult.NFailure
import com.sundayting.wancompose.network.okhttp.NResult.NSuccess
import com.sundayting.wancompose.network.okhttp.exception.FailureReason
import okhttp3.Headers
import retrofit2.Response
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * 网络请求结果
 *
 * 有以下子类：
 *
 * [NSuccess] 网络请求成功
 *
 * [NFailure] 网络请求失败
 *
 */
sealed class NResult<out T> private constructor() {

    /**
     * 网络请求成功
     */
    class NSuccess<T> internal constructor(private val response: Response<T>) : NResult<T>() {

        init {
            check(response.body() != null) { "不要把body为空的response传入${NSuccess::class.simpleName}" }
            check(response.isSuccessful) { "不要把失败的response传入${NSuccess::class.simpleName}中" }
        }

        override fun toString(): String =
            "[${NSuccess::class.simpleName}](body=${body},headers=${headers},code=${httpCode})"

        val body by lazy { response.body()!! }
        val httpCode by lazy { response.code() }
        val httpMessage: String by lazy { response.message() }
        val rawResponse: okhttp3.Response by lazy { response.raw() }
        val headers: Headers by lazy { response.headers() }
    }

    /**
     * 网络请求失败
     */
    class NFailure internal constructor(val failureReason: FailureReason) : NResult<Nothing>() {
        override fun toString(): String =
            "[${NFailure::class.simpleName}](message=${failureReason.message})"
    }

}

/**
 * 提取结果中的实体类，如果是成功请求，则返回实体类，如果为失败请求则返回空
 * @return 网络请求结果对应的实体类
 */
fun <T> NResult<T>.getOrNull(): T? {
    return if (isNSuccess()) {
        body
    } else {
        null
    }
}

/**
 * 提取结果中的实体类，如果是成功请求，则返回实体类，如果为失败请求则返回默认值
 * @param defaultValue 默认值
 * @return 网络请求结果对应的实体类
 */
fun <R, T : R> NResult<T>.getOrDefault(defaultValue: R): R {
    return if (isNSuccess()) {
        body
    } else {
        defaultValue
    }
}

/**
 * 提取结果中的实体类，如果是成功，则返回实体类，如果失败则根据[onFailure]返回备选值
 * @param onFailure 如果网络请求失败时的回调
 * @return 网络请求结果对应的实体类
 */
fun <R, T : R> NResult<T>.getOrElse(
    onFailure: (reason: FailureReason) -> R,
): R {
    return if (isNSuccess()) {
        body
    } else {
        onFailure(failureReason)
    }
}

/**
 * 提取结果中的实体类，如果是成功请求，则返回实体类，如果为失败请求则抛出异常
 * @return 网络请求结果对应的实体类
 */
@Throws(Throwable::class)
fun <T> NResult<T>.getOrThrow(): T {
    if (isNSuccess()) {
        return body
    } else {
        throw failureReason.cause
    }
}

/**
 * 当前网络请求是否是成功请求
 */
@OptIn(ExperimentalContracts::class)
fun <T> NResult<T>.isNSuccess(): Boolean {
    contract {
        returns(true) implies (this@isNSuccess is NResult.NSuccess)
        returns(false) implies (this@isNSuccess is NResult.NFailure)
    }
    return this is NResult.NSuccess
}

/**
 * 网络请求成功时回调
 */
@OptIn(ExperimentalContracts::class)
fun <T> NResult<T>.ifNSuccess(onSuccess: (NResult.NSuccess<T>) -> Unit): NResult<T> {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    }
    if (isNSuccess()) {
        onSuccess(this)
    }
    return this
}

/**
 * 网络请求成功时回调（suspend版）
 */
@OptIn(ExperimentalContracts::class)
suspend fun <T> NResult<T>.suspendIfNSuccess(onSuccess: suspend (NResult.NSuccess<T>) -> Unit): NResult<T> {
    contract {
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    }
    if (isNSuccess()) {
        onSuccess(this)
    }
    return this
}

/**
 * 网络请求失败时回调
 */
@OptIn(ExperimentalContracts::class)
fun <T> NResult<T>.ifNFailure(onFailure: (NResult.NFailure) -> Unit): NResult<T> {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    if (!isNSuccess()) {
        onFailure(this)
    }
    return this
}

/**
 * 网络请求失败时回调（suspend版）
 */
@OptIn(ExperimentalContracts::class)
suspend fun <T> NResult<T>.suspendIfNFailure(onFailure: suspend (NResult.NFailure) -> Unit): NResult<T> {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
    }
    if (!isNSuccess()) {
        onFailure(this)
    }
    return this
}



