package com.sundayting.wancompose.page.examplewidgetscreen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

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

@Composable
@Preview(showBackground = true)
private fun PreviewTest() {
    val state = rememberPagerState { 10 }
    val scope = rememberCoroutineScope()
    val percentage by remember {
        derivedStateOf { state.getOffsetDistanceInPages(state.targetPage) }
    }

    val myPage by remember {
        derivedStateOf {
            state.currentPageOffsetFraction
            val percentage = state.getOffsetDistanceInPages(state.targetPage)
            if (percentage.absoluteValue >= 0.4f) {
                state.settledPage
            } else {
                state.targetPage
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                (0..9).forEach {
                    Box(
                        Modifier
                            .size(100.dp)
                            .background(
                                if (myPage == it) Color.Red else Color.Red.copy(
                                    0.5f
                                )
                            )
                            .clickable {
                                scope.launch {
                                    state.animateScrollToPage(it)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(it.toString())
                    }
                }
            }

            HorizontalPager(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("$it")
                }
            }
        }
        Text(
            "curPage:${state.currentPage}\ntargetPage:${state.targetPage}\nmyPage:${myPage}\nsettlePage:${state.settledPage}\npercentage:${percentage}",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}