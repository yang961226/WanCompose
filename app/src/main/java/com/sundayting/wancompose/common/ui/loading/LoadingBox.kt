package com.sundayting.wancompose.common.ui.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

val LocalLoadingBoxIsLoading = staticCompositionLocalOf { false }

private enum class LoadingBoxEnum {
    LoadingContent,
    Content
}

@Composable
fun LoadingBox(
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {

    val isLoading = LocalLoadingBoxIsLoading.current

    SubcomposeLayout(modifier = modifier, measurePolicy = { constraint ->

        val contentPlaceables =
            subcompose(LoadingBoxEnum.Content, content).map {
                it.measure(
                    constraint.copy(
                        minWidth = 0,
                        minHeight = 0
                    )
                )
            }

        val maxWidth = contentPlaceables.maxOfOrNull { it.width } ?: 0
        val maxHeight = contentPlaceables.maxOfOrNull { it.height } ?: 0

        val loadingContentPlaceables =
            subcompose(LoadingBoxEnum.LoadingContent, loadingContent).map {
                it.measure(
                    constraint.copy(
                        maxWidth = maxWidth,
                        maxHeight = maxHeight,
                        minWidth = 0,
                        minHeight = 0
                    )
                )
            }

        layout(maxWidth, maxHeight) {
            if (isLoading) {
                loadingContentPlaceables.forEach { it.place(IntOffset.Zero) }
            } else {
                contentPlaceables.forEach { it.place(IntOffset.Zero) }
            }
        }
    })

}

@Composable
@Preview
private fun PreviewLoadingBox() {
    var isLoading by remember {
        mutableStateOf(true)
    }
    val loadingBox: @Composable () -> Unit = remember {
        {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("加载中")
            }
        }
    }
    CompositionLocalProvider(
        LocalLoadingBoxIsLoading provides isLoading
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LoadingBox(
                loadingContent = loadingBox,
                content = {
                    Box(
                        Modifier
                            .size(300.dp, 170.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("我是内容A")
                    }
                }
            )
            LoadingBox(
                loadingContent = loadingBox,
                content = {
                    Box(
                        Modifier
                            .size(120.dp, 80.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Green),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("我是内容B")
                    }
                }
            )

            Button(onClick = { isLoading = !isLoading }) {
                Text("点击切换")
            }
        }

    }

}