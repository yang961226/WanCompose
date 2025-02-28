package com.sundayting.wancompose.page.examplewidgetscreen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.examplewidgetscreen.pointinput.PointInput
import com.sundayting.wancompose.page.examplewidgetscreen.scrollaletabrow.TabRowScreen
import com.sundayting.wancompose.page.examplewidgetscreen.tantancard.TanTanSwipeCardScreen
import com.sundayting.wancompose.page.examplewidgetscreen.viewpager.ViewPagerHorizontalPagerNestScroll
import com.sundayting.wancompose.page.homescreen.HomeScreen
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme

class ExampleCardBean(
    val name: String,
    @DrawableRes val resId: Int,
)

@Composable
private fun ExampleCardItem(
    modifier: Modifier = Modifier,
    bean: ExampleCardBean,
    onClick: (ExampleCardBean) -> Unit = {},
) {

    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                color = WanTheme.colors.level4BackgroundColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onClick(bean) }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = bean.resId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Spacer(Modifier.height(10.dp))
        Text(
            bean.name, style = WanTheme.typography.h6.copy(
                WanTheme.colors.level1TextColor
            )
        )
    }

}

object ExampleWidget : HomeScreen.HomeScreenPage {
    override val route: String
        get() = "代码案例"

    private val exampleList = listOf(
        TanTanSwipeCardScreen.exampleCardBean,
        PointInput.exampleCardBean,
//        NestScroll.exampleCardBean,
        ViewPagerHorizontalPagerNestScroll.exampleCardBean,
        TabRowScreen.exampleCardBean
    )

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        onClick: (ExampleCardBean) -> Unit,
    ) {
        TitleBarWithContent(
            modifier,
            titleBarContent = {
                Text(
                    stringResource(id = R.string.bottom_tab_example),
                    style = TitleTextStyle,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        ) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WanTheme.colors.level2BackgroundColor)
                    .padding(horizontal = 20.dp),
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp,
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                items(exampleList) {
                    ExampleCardItem(Modifier.fillMaxWidth(), it, onClick = onClick)
                }
            }
        }

    }
}