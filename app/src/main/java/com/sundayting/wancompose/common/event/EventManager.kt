package com.sundayting.wancompose.common.event

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventManager @Inject constructor() {
    interface Event

    private val scope = MainScope()

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun emitEvent(event: Event) {
        scope.launch {
            _eventFlow.emit(event)
        }
    }
}

fun EventManager.emitToast(content: String, isLong: Boolean = false) {
    emitEvent(ToastEvent(content, isLong))
}

val LocalEventManager = compositionLocalOf<EventManager> {
    error("未初始化${EventManager::class.java.simpleName}")
}

class ToastEvent(
    val content: String,
    val isLong: Boolean = false,
) : EventManager.Event