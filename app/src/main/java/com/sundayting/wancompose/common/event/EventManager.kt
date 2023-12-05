package com.sundayting.wancompose.common.event

import android.content.Context
import com.sundayting.wancompose.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

object EventManager {
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

fun EventManager.emitNeedLoginAgain() {
    emitEvent(NeedLoginAgainEvent)
}

fun EventManager.emitCollectArticleEvent(
    context: Context,
    id: Long,
    isCollect: Boolean,
) {
    if (isCollect) {
        emitToast(context.getString(R.string.article_collect_success))
    } else {
        emitToast(context.getString(R.string.article_uncollect_success))
    }
    emitEvent(ArticleCollectChangeEvent(id, isCollect))
}

class ToastEvent(
    val content: String,
    val isLong: Boolean = false,
) : EventManager.Event

object NeedLoginAgainEvent : EventManager.Event

class ArticleCollectChangeEvent(
    val id: Long,
    val isCollect: Boolean,
) : EventManager.Event

object ShowLoginPageEvent : EventManager.Event