package com.sundayting.wancompose.network

import kotlinx.serialization.Serializable

@Serializable
abstract class WanNResult<T> {
    abstract val data: T?
    abstract val errorCode: Int
    abstract val errorMsg: String
}

class WanError(val errorCode: Int, val errorMsg: String) : Throwable()