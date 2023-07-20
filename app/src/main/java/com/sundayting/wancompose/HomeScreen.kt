package com.sundayting.wancompose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sundayting.wancompose.ui.title.TitleBar
import kotlinx.coroutines.launch

object HomeScreen : WanComposeDestination {

    override val route: String
        get() = "首页"

    @Composable
    override fun Screen() {
        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {

            val pagerState = rememberPagerState()
            val scope = rememberCoroutineScope()

            HomeTitle()
            HomeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false),
                pagerState = pagerState
            )
            HomeBottomNavigation.Content(
                modifier = Modifier.fillMaxWidth(),
                page = pagerState.currentPage,
                onPageChanged = {
                    scope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }
            )
        }
    }

    @Composable
    private fun HomeTitle(modifier: Modifier = Modifier) {
        TitleBar(modifier) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "首页",
            )
        }
    }

    @Composable
    private fun HomeContent(
        modifier: Modifier = Modifier,
        pagerState: PagerState = rememberPagerState(),
    ) {

        HorizontalPager(
            modifier = modifier,
            pageCount = 5,
            state = pagerState
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("第${it}页")
            }
        }
    }

    private object HomeBottomNavigation {

        private data class BottomBean(
            @DrawableRes val resId: Int,
            @StringRes val titleId: Int,
        )

        private val list = listOf(
            BottomBean(
                resId = R.drawable.ic_launcher_background,
                titleId = R.string.bottom_tab_home
            ),
            BottomBean(
                resId = R.drawable.ic_launcher_background,
                titleId = R.string.bottom_tab_system
            ),
            BottomBean(
                resId = R.drawable.ic_launcher_background,
                titleId = R.string.bottom_tab_project
            ),
            BottomBean(
                resId = R.drawable.ic_launcher_background,
                titleId = R.string.bottom_tab_official_account
            ),
            BottomBean(
                resId = R.drawable.ic_launcher_background,
                titleId = R.string.bottom_tab_mine
            ),
        )

        @Composable
        fun Content(
            modifier: Modifier = Modifier,
            page: Int = 0,
            onPageChanged: (Int) -> Unit = {},
        ) {

            Row(
                modifier
                    .fillMaxWidth()
                    .background(Color.Blue)
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                list.forEachIndexed { index, bottomBean ->
                    HomeBottomNavigationItem(
                        modifier = Modifier.clickable {
                            onPageChanged(index)
                        },
                        bean = bottomBean,
                        isSelected = index == page
                    )
                }
            }

        }

        @Composable
        private fun HomeBottomNavigationItem(
            modifier: Modifier = Modifier,
            bean: BottomBean,
            isSelected: Boolean,
        ) {

            Column(
                modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = bean.resId),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    contentScale = ContentScale.Crop,
                    colorFilter = remember(isSelected) {
                        if (isSelected) ColorFilter.tint(Color.Red) else null
                    }
                )
                Text(
                    text = stringResource(id = bean.titleId),
                )
            }

        }
    }


}