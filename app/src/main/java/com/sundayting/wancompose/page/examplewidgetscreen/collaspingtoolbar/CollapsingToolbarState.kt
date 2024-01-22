package com.sundayting.wancompose.page.examplewidgetscreen.collaspingtoolbar

import androidx.annotation.FloatRange
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

class CollapsingToolbarState(
    initial: Int = Int.MAX_VALUE,
) : ScrollableState {

    var height by mutableIntStateOf(initial)
        private set

    var minHeight: Int
        get() = minHeightState
        internal set(value) {
            minHeightState = value

            if (height < value) {
                height = value
            }
        }

    var maxHeight: Int
        get() = maxHeightState
        internal set(value) {
            maxHeightState = value

            if (value < height) {
                height = value
            }
        }

    private var maxHeightState by mutableIntStateOf(Int.MAX_VALUE)
    private var minHeightState by mutableIntStateOf(0)

    val progress: Float
        @FloatRange(from = 0.0, to = 1.0)
        get() =
            if (minHeight == maxHeight) {
                0f
            } else {
                ((height - minHeight).toFloat() / (maxHeight - minHeight)).coerceIn(0f, 1f)
            }

    private var deferredConsumption: Float = 0f

    private val scrollableState = ScrollableState { value ->
        val consume = if (value < 0) {
            max(minHeight.toFloat() - height, value)
        } else {
            min(maxHeight.toFloat() - height, value)
        }

        val current = consume + deferredConsumption
        val currentInt = current.toInt()

        if (current.absoluteValue > 0) {
            height += currentInt
            deferredConsumption = current - currentInt
        }

        consume
    }

    override val isScrollInProgress: Boolean
        get() = scrollableState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float = scrollableState.dispatchRawDelta(delta)

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit,
    ) = scrollableState.scroll(scrollPriority, block)
}