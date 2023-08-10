package com.sundayting.wancompose.network

import kotlinx.serialization.Serializable

@Serializable
abstract class WanNResult<T> {
    abstract val data: T?
    abstract val errorCode: Int
    abstract val errorMsg: String
}

fun <T> WanNResult<T>.requireData(): T {
    return data!!
}

@Serializable
class WanEmptyNResult(
    override val data: Unit = Unit,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<Unit>()

class WanError(val errorCode: Int, val errorMsg: String) : Throwable()