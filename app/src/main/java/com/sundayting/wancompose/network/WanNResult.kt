package com.sundayting.wancompose.network

data class WanNResult<T>(
    val data: T?,
    val errorCode: Int,
    val errorMsg: String,
)

fun WanNResult<*>.isSuccessful(): Boolean {
    return errorCode == 0
}

fun WanNResult<*>.needLoginAgain(): Boolean {
    return errorCode == -1001
}

class WanException(
    val msg: String,
) : Exception()