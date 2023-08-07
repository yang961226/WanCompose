package com.sundayting.wancompose.network

data class WanNResult<T>(
    val data: T?,
    val errorCode: Int,
    val errorMsg: String,
)