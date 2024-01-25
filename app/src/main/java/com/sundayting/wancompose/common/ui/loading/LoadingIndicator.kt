package com.sundayting.wancompose.common.ui.loading

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sundayting.wancompose.R
import com.sundayting.wancompose.theme.WanTheme

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {

    val rotateTransition = rememberInfiniteTransition(label = "")
    val angle by rotateTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000)), label = ""
    )

    Image(
        modifier = modifier.graphicsLayer {
            rotationZ = angle
        },
        painter = painterResource(id = R.drawable.ic_loading),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(WanTheme.colors.primaryColor)
    )

}

@Composable
@Preview
private fun PreviewLoadingIndicator() {
    LoadingIndicator(Modifier.size(30.dp))
}