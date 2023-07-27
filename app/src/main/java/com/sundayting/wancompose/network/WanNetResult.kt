package com.sundayting.wancompose.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class WanNetResult<T> {
    @SerialName("data")
    abstract val data: T?

    @SerialName("errorCode")
    abstract val errorCode: Int

    @SerialName("errorMsg")
    abstract val errorMsg: String
}