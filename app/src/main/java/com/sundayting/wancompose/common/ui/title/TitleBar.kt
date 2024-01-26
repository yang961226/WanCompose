package com.sundayting.wancompose.common.ui.title

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Surface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sundayting.wancompose.R
import com.sundayting.wancompose.theme.WanTheme

@Composable
fun BoxScope.TitleBarWithBackButtonContent(
    onClickBackButton: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Image(
        painter = painterResource(id = R.drawable.ic_back),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(start = 20.dp)
            .size(25.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(radius = 25.dp, bounded = false),
            ) { onClickBackButton() },
        colorFilter = ColorFilter.tint(WanTheme.colors.level1TextColor)
    )
    content()
}

@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    properties: TitleBarProperties = DEFAULT_PROPERTIES,
    content: @Composable BoxScope.() -> Unit,
) {


    Surface(
        elevation = properties.elevation,
        modifier = modifier,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(properties.backgroundColor ?: WanTheme.colors.level2BackgroundColor)
                .statusBarsPadding()
                .height(45.dp)
        ) {
            content()
        }
    }
}

data class TitleBarProperties(
    val elevation: Dp = 5.dp,
    val backgroundColor: Color? = null,
)

private val DEFAULT_PROPERTIES = TitleBarProperties()

@Composable
fun TitleBarWithContent(
    modifier: Modifier = Modifier,
    properties: TitleBarProperties = DEFAULT_PROPERTIES,
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

        TitleBar(
            modifier = Modifier.constrainAs(titleBarBodyContent) {
                top.linkTo(parent.top)
            },
            properties = properties
        ) {
            titleBarContent?.invoke(this)
        }
    }
}