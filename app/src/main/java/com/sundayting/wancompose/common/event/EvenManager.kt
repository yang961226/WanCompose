package com.sundayting.wancompose.common.event

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EvenManager @Inject constructor() {

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

class ToastEvent(
    val content: String,
    val isLong: Boolean = false,
) : EvenManager.Event