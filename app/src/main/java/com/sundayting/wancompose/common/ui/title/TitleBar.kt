package com.sundayting.wancompose.common.ui.title

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

private val backgroundColor = Color(0xFF5380ec)

@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {


    Surface(
        elevation = 10.dp,
        modifier = modifier
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .statusBarsPadding()
                .height(45.dp)
        ) {
            content()
        }
    }
}

@Composable
fun TitleBarWithContent(
    modifier: Modifier = Modifier,
    titleBarContent: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {

    ConstraintLayout(modifier) {
        val (
            titleBarBodyContent,
            bodyContent,
        ) = createRefs()


        Box(Modifier.constrainAs(bodyContent) {
            top.linkTo(titleBarBodyContent.bottom)
            bottom.linkTo(parent.bottom)
            height = Dimension.fillToConstraints
            width = Dimension.matchParent
        }) {
            content()
        }

        TitleBar(Modifier.constrainAs(titleBarBodyContent) {
            top.linkTo(parent.top)
        }) {
            titleBarContent?.invoke(this)
        }
    }
}