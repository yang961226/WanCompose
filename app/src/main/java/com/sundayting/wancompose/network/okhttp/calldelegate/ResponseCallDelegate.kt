package com.sundayting.wancompose.network.okhttp.calldelegate

import com.sundayting.wancompose.network.okhttp.NResult
import com.sundayting.wancompose.network.okhttp.exception.FailureReason
import com.sundayting.wancompose.network.okhttp.exception.ServerErrorException
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ResultTransformer<T> {

    fun onHttpException(
        t: Throwable,
    ): NResult<T>

    fun onHttpSuccess(
        response: Response<T>,
    ): NResult<T>

}

/**
 * retrofit的[Call]代理类，将网络请求统一返回[NResult]
 */
internal class ResponseCallDelegate<T>(
    private val proxyCall: Call<T>,
    private val resultTransformer: ResultTransformer<T>,
) : Call<NResult<T>> {

    override fun enqueue(callback: Callback<NResult<T>>) =
        proxyCall.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(
                    this@ResponseCallDelegate,
                    Response.success(
                        try {
                            if (response.isSuccessful) {
                                response.toNSuccess()
                            } else {
                                resultTransformer.onHttpSuccess(response)
                            }
                        } catch (t: Throwable) {
                            resultTransformer.onHttpException(t)
                        }
                    )
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@ResponseCallDelegate,
                    Response.success(resultTransformer.onHttpException(t))
                )
            }

        })

    override fun isExecuted(): Boolean = proxyCall.isExecuted

    override fun cancel() = proxyCall.cancel()

    override fun isCanceled(): Boolean = proxyCall.isCanceled

    override fun request(): Request = proxyCall.request()

    override fun timeout(): Timeout = proxyCall.timeout()

    override fun clone(): Call<NResult<T>> =
        ResponseCallDelegate(proxyCall.clone(), resultTransformer)

    override fun execute(): Response<NResult<T>> = throw NotImplementedError()


}

/**
 * 将[Response]转译为[NResult.NSuccess]
 */
fun <T> Response<T>.toNSuccess(): NResult.NSuccess<T> {
    assert(isSuccessful) { "严禁将失败的http请求转成成功结果！" }
    return NResult.NSuccess(this)
}

/**
 * 将[Response]转译为[NResult.NFailure]，这种情况只发生于HTTP协议错误的情况下
 *
 * 其中，[NResult.NFailure]的异常会通过[FailureReason]包裹
 */
fun Response<*>.toNFailure(
    failureTransform: (Throwable) -> NResult.NFailure,
): NResult.NFailure {
    assert(!isSuccessful) { "严禁将成功的http请求转成失败结果！" }
    return failureTransform(ServerErrorException(code(), errorBody()))
}
