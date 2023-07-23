package com.sundayting.wancompose.common.ui.title

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

private val backgroundColor = Color(0xFF5380ec)

@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {


    Box(
        modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .statusBarsPadding()
            .height(35.dp),
    ) {
        content()
    }


}