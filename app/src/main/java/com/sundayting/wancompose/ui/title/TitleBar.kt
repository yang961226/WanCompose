package com.sundayting.wancompose.ui.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {

    Box(
        modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .statusBarsPadding()
            .height(50.dp),
    ) {
        content()
    }


}