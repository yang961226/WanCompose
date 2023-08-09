package com.sundayting.wancompose.network.okhttp.calldelegate

import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitNeedLoginAgain
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.network.WanException
import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.network.isSuccessful
import com.sundayting.wancompose.network.needLoginAgain
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
) : Call<NResult<T>> {

    override fun enqueue(callback: Callback<NResult<T>>) =
        proxyCall.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(
                    this@ResponseCallDelegate,
                    Response.success(
                        try {
                            if (response.isSuccessful) {
                                response.body()!!.let { body ->
                                    if (body is WanNResult<*>) {
                                        if (body.isSuccessful()) {
                                            NResult.NSuccess(response)
                                        } else {
                                            if (body.needLoginAgain()) {
                                                EventManager.emitNeedLoginAgain()
                                            }
                                            EventManager.emitToast(body.errorMsg)
                                            NResult.NFailure(
                                                FailureReason(
                                                    body.errorMsg,
                                                    WanException(body.errorMsg)
                                                )
                                            )
                                        }
                                    } else {
                                        NResult.NSuccess(response)
                                    }
                                }
                            } else {
                                NResult.NFailure(
                                    FailureReason(
                                        "网络异常",
                                        ServerErrorException(
                                            response.code(),
                                            response.errorBody()
                                        )
                                    )
                                )
                            }
                        } catch (t: Throwable) {
                            NResult.NFailure(FailureReason("网络异常", t))
                        }
                    )
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@ResponseCallDelegate,
                    Response.success(
                        NResult.NFailure(
                            FailureReason(
                                "网络异常",
                                t
                            )
                        )
                    )
                )
            }

        })

    override fun isExecuted(): Boolean = proxyCall.isExecuted

    override fun cancel() = proxyCall.cancel()

    override fun isCanceled(): Boolean = proxyCall.isCanceled

    override fun request(): Request = proxyCall.request()

    override fun timeout(): Timeout = proxyCall.timeout()

    override fun clone(): Call<NResult<T>> =
        ResponseCallDelegate(proxyCall.clone())

    override fun execute(): Response<NResult<T>> = throw NotImplementedError()


}

/**
 * 将[Response]转译为[NResult.NSuccess]
 */
fun <T> Response<T>.toNSuccess(): NResult.NSuccess<T> {
    assert(isSuccessful) { "严禁将失败的http请求转成成功结果！" }
    return NResult.NSuccess(this)
}
