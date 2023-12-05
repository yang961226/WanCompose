package com.sundayting.wancompose.common.event

import android.content.Context
import androidx.annotation.StringRes
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeApplication
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventManager @Inject constructor(
    @ApplicationContext val context: Context,
) {
    interface Event

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface EventManagerProviderEntryPoint {
        fun eventManager(): EventManager
    }

    companion object {

        fun getInstance(): EventManager {
            val entryPoint = EntryPointAccessors.fromApplication(
                WanComposeApplication.instance,
                EventManagerProviderEntryPoint::class.java
            )
            return entryPoint.eventManager()
        }

    }

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

fun EventManager.emitToast(isLong: Boolean = false, stringGetter: (Context) -> String) {
    emitEvent(ToastEvent(stringGetter(context)))
}

fun EventManager.emitToast(@StringRes stringId: Int, isLong: Boolean = false) {
    emitEvent(ToastEvent(context.getString(stringId), isLong))
}

fun EventManager.emitNeedLoginAgain() {
    emitEvent(NeedLoginAgainEvent)
}

fun EventManager.emitCollectArticleEvent(
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