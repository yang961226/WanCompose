package com.sundayting.wancompose.network.okhttp.exception


/**
 * 网络异常原因
 * @param message 错误的原因，能够被人直接阅读的信息，非原生错误栈信息
 * @param cause 原始异常，即触发网络错误的真正异常
 */
class FailureReason internal constructor(
    val message: String,
    val cause: Throwable,
)
