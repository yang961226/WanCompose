package com.sundayting.wancompose.network

import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitToast
import kotlinx.coroutines.CoroutineExceptionHandler

val NetExceptionHandler = CoroutineExceptionHandler { _, _ ->
    EventManager.emitToast("网络异常，请检查网络")
}


