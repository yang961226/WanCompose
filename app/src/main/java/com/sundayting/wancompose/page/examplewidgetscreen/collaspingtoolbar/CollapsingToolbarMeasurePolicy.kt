package com.sundayting.wancompose.page.examplewidgetscreen.collaspingtoolbar

import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints

private class CollapsingToolbarMeasurePolicy(
    private val collapsingToolbarState: CollapsingToolbarState,
) : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val placeables = measurables.map {
            it.measure(
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
//                    maxHeight = Constraints.Infinity
                )
            )
        }
        val placeStrategy = measurables.map { it.parentData }
        val minHeight = (placeables.minOfOrNull { it.height } ?: 0).coerceIn(
            constraints.minHeight, constraints.maxHeight
        )
        val maxHeight = (placeables.maxOfOrNull { it.height } ?: 0).coerceIn(
            constraints.minHeight, constraints.maxHeight
        )
        val maxWidth = (placeables.maxOfOrNull { it.width } ?: 0).coerceIn(
            constraints.minWidth, constraints.maxWidth
        )

        collapsingToolbarState.maxHeight = maxHeight
        collapsingToolbarState.minHeight = minHeight
        val height = collapsingToolbarState.height
        return layout(maxWidth, height) {

        }
    }
}