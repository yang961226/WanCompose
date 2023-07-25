package com.sundayting.wancompose.homescreen

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
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBar
import com.sundayting.wancompose.homescreen.article.ui.ArticleList
import kotlinx.coroutines.launch

object HomeScreen : WanComposeDestination {

    override val route: String
        get() = "首页"

    sealed class HomeScreenPage(
        val route: String,
    ) {

        object ArticleList : HomeScreenPage("文章列表")
        object System : HomeScreenPage("体系")
        object Mine : HomeScreenPage("我的")


        data class BottomItem(
            @DrawableRes val resId: Int,
            @StringRes val titleId: Int,
            val page: HomeScreenPage,
        )

    }

    val pageList = listOf(
        HomeScreenPage.ArticleList, HomeScreenPage.System, HomeScreenPage.Mine
    )

    val bottomItemList = listOf(
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_home,
            titleId = R.string.bottom_tab_home,
            page = HomeScreenPage.ArticleList
        ),
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_system,
            titleId = R.string.bottom_tab_system,
            page = HomeScreenPage.System
        ),
//    BottomItem(
//        resId = R.drawable.ic_project,
//        titleId = R.string.bottom_tab_project
//    ),
//    BottomItem(
//        resId = R.drawable.ic_official_account,
//        titleId = R.string.bottom_tab_official_account
//    ),
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_mine,
            titleId = R.string.bottom_tab_mine,
            page = HomeScreenPage.Mine
        ),
    )

    @Composable
    fun Navigation(
        navController: NavHostController,
    ) {

        val entry by navController.currentBackStackEntryAsState()
        val curRoute by remember {
            derivedStateOf {
                entry?.destination?.route
            }
        }
        BottomNavigation(
            backgroundColor = Color.White,
        ) {
            bottomItemList.forEach { bottomItem ->
                BottomNavigationItem(
                    selectedContentColor = Color(0xFF5380ec),
                    unselectedContentColor = Color.Gray,
                    selected = bottomItem.page.route == curRoute,
                    onClick = {
                        navController.navigate(bottomItem.page.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = bottomItem.resId),
                            contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    },
                    label = {
                        Text(stringResource(id = bottomItem.titleId))
                    }
                )
            }
        }
    }

    fun NavGraphBuilder.homeNavGraph(
        navController: NavHostController,
    ) {
        navigation(route = HomeScreen.route, startDestination = HomeScreenPage.ArticleList.route) {
            composable(HomeScreenPage.ArticleList.route) {
                Column(Modifier.fillMaxSize()) {
                    TitleBar(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF5380ec))
                    ) {
                        Text(
                            "标题", style = TextStyle(
                                fontSize = 16.sp, color = Color.White
                            ), modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("我是1")
                    }
                }
            }
            composable(HomeScreenPage.System.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("我是2")
                }
            }
            composable(HomeScreenPage.Mine.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("我是3")
                }
            }
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        viewModel: HomeScreenViewModel = hiltViewModel(),
        toWebLink: (String) -> Unit = {},
        toLogin: () -> Unit = {},
    ) {
        Column(
            modifier
                .navigationBarsPadding()
        ) {

            val pagerState = rememberPagerState()
            val scope = rememberCoroutineScope()

            HomeTitle()
            HomeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false),
                pagerState = pagerState,
                articleListState = viewModel.articleListState,
                toWebLink = toWebLink,
            )
            HomeBottomNavigation.Content(
                modifier = Modifier.fillMaxWidth(),
                page = pagerState.currentPage,
                onPageChanged = {
                    if (it == 4) {
                        toLogin()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(it)
                        }
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
                text = stringResource(id = R.string.bottom_tab_home),
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.White
                )
            )
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(20.dp)
                    .align(Alignment.CenterEnd),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }

    @Composable
    private fun HomeContent(
        modifier: Modifier = Modifier,
        pagerState: PagerState = rememberPagerState(),
        articleListState: HomeScreenViewModel.ArticleListState,
        toWebLink: (String) -> Unit = {},
    ) {

        HorizontalPager(
            modifier = modifier,
            pageCount = 5,
            state = pagerState
        ) { page ->
            if (page == 0) {
                ArticleList.ArticleScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    articleListState = articleListState,
                    toWebLink = toWebLink
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("第${page}页")
                }
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
                resId = R.drawable.ic_home,
                titleId = R.string.bottom_tab_home
            ),
            BottomBean(
                resId = R.drawable.ic_system,
                titleId = R.string.bottom_tab_system
            ),
            BottomBean(
                resId = R.drawable.ic_project,
                titleId = R.string.bottom_tab_project
            ),
            BottomBean(
                resId = R.drawable.ic_official_account,
                titleId = R.string.bottom_tab_official_account
            ),
            BottomBean(
                resId = R.drawable.ic_mine,
                titleId = R.string.bottom_tab_mine
            ),
        )

        @Composable
        fun Content(
            modifier: Modifier = Modifier,
            page: Int = 0,
            onPageChanged: (Int) -> Unit = {},
        ) {

            Divider(Modifier.fillMaxWidth(), color = Color.Gray.copy(0.2f))
            Row(
                modifier
                    .fillMaxWidth()
                    .background(Color(0xFFf7f7f7))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                list.forEachIndexed { index, bottomBean ->
                    HomeBottomNavigationItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, false)
                            .clickable {
                                onPageChanged(index)
                            }
                            .padding(vertical = 10.dp),
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
                    modifier = Modifier.size(25.dp),
                    contentScale = ContentScale.Crop,
                    colorFilter = remember(isSelected) {
                        if (isSelected) ColorFilter.tint(Color(0xFF5380ec)) else ColorFilter.tint(
                            Color(0xFF999999)
                        )
                    }
                )
                Text(
                    text = stringResource(id = bean.titleId),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                )
            }

        }
    }


}