package com.sundayting.wancompose.common.event

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.staticCompositionLocalOf
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeApplication
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
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

val LocalEventManager = staticCompositionLocalOf<EventManager> { error("找不到EventManager") }

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

        fun getInstance(
            application: Application? = null,
        ): EventManager {
            val entryPoint = EntryPointAccessors.fromApplication(
                application ?: WanComposeApplication.instance,
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
    bean: ArticleList.ArticleUiBean,
    tryCollect: Boolean,
) {
    if (!bean.isCollect) {
        emitToast(context.getString(R.string.article_collect_success))
    } else {
        emitToast(context.getString(R.string.article_uncollect_success))
    }
    emitEvent(ArticleCollectChangeEvent(bean, tryCollect))
}

class ToastEvent(
    val content: String,
    val isLong: Boolean = false,
) : EventManager.Event

object NeedLoginAgainEvent : EventManager.Event

class ArticleCollectChangeEvent(
    val bean: ArticleList.ArticleUiBean,
    val tryCollect: Boolean,
) : EventManager.Event

class ArticleSharedChangeEvent(
    val bean: ArticleList.ArticleUiBean,
) : EventManager.Event

class ArticleSharedDeleteEvent(
    val bean: ArticleList.ArticleUiBean,
) : EventManager.Event

object ShowLoginPageEvent : EventManager.Event

object ShareArticleSuccess : EventManager.Event