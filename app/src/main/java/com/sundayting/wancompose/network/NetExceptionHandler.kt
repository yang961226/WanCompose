package com.sundayting.wancompose.network

import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitToast
import kotlinx.coroutines.CoroutineExceptionHandler


val NetExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    if (throwable is WanError) {
        EventManager.getInstance().emitToast(throwable.errorMsg)
    } else {
        EventManager.getInstance().emitToast("网络异常，请检查网络")
    }
}


