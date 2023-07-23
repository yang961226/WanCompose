package com.sundayting.wancompose.network

import kotlinx.serialization.Serializable

@Serializable
abstract class WanNetResult<T> {
    abstract val data: T?
    abstract val errorCode: Int
    abstract val errorMsg: String
}