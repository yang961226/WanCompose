package com.sundayting.wancompose.page.homescreen.mine.point

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sundayting.wancompose.LocalLoginUser
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.ktx.onBottomReached
import com.sundayting.wancompose.common.ui.title.TitleBarProperties
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.mine.point.PointScreen.PointRecordContent
import com.sundayting.wancompose.page.homescreen.mine.point.repo.PointViewModel
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date

object PointScreen : WanComposeDestination {

    fun NavController.navigateToPointScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    override val route: String
        get() = "积分页面"

    @Stable
    class PointState {
        private val _pointRecordList = mutableStateListOf<GetPointRecord>()
        val pointRecordList: List<GetPointRecord> = _pointRecordList

        fun addRecord(list: List<GetPointRecord>) {
            _pointRecordList.addAll(list)
        }

        var isLoading by mutableStateOf(false)

        var canLoadMore by mutableStateOf(true)
    }

    data class GetPointRecord(
        val id: Int,
        val title: String,
        val date: Long,
        val points: Int,
    )

    @Composable
    fun Content(
        modifier: Modifier = Modifier,
        state: PointState,
        onClickBackButton: () -> Unit,
        onLoadMore: () -> Unit,
    ) {
        TitleBarWithContent(
            modifier,
            properties = TitleBarProperties(
                elevation = 0.dp,
                backgroundColor = WanTheme.colors.primaryColor
            ),
            titleBarContent = {
                TitleBarWithBackButtonContent(
                    onClickBackButton = onClickBackButton
                ) {
                    Text(
                        stringResource(id = R.string.my_points),
                        style = TitleTextStyle.copy(
                            color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                val number = remember { Animatable(0f) }
                val loginUser = LocalLoginUser.current
                val listIsEmpty by remember {
                    derivedStateOf { state.pointRecordList.isEmpty() }
                }
                LaunchedEffect(Unit) {
                    snapshotFlow { loginUser?.coinCount ?: 0 }.map { it.toFloat() }.collect {
                        number.animateTo(it, animationSpec = tween(1000))
                    }
                }
                val lazyColumnState = rememberLazyListState()
                lazyColumnState.onBottomReached {
                    onLoadMore()
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(WanTheme.colors.primaryColor)
                ) {
                    Text(
                        text = number.value.toInt().toString(),
                        style = TextStyle(fontSize = 70.sp, color = Color.White),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Crossfade(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WanTheme.colors.level1BackgroundColor),
                    targetState = state.isLoading && listIsEmpty,
                    label = ""
                ) { isShowLoading ->
                    if (isShowLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = WanTheme.colors.primaryColor
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            state = lazyColumnState,
                            contentPadding = PaddingValues(bottom = 10.dp)
                        ) {
                            items(
                                state.pointRecordList,
                                key = { it.id },
                                contentType = { 1 }) { record ->
                                PointRecordContent(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateItemPlacement(),
                                    state = record
                                )
                                Divider(
                                    Modifier.fillMaxWidth(),
                                    color = WanTheme.colors.level4BackgroundColor
                                )
                            }
                            if (state.isLoading && listIsEmpty.not()) {
                                item {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = WanTheme.colors.primaryColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        viewModel: PointViewModel = hiltViewModel(),
        onClickBackButton: () -> Unit,
    ) {
        Content(
            modifier,
            state = viewModel.state,
            onClickBackButton = onClickBackButton,
            onLoadMore = viewModel::loadMore
        )
    }

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA)

    @Composable
    fun PointRecordContent(
        modifier: Modifier = Modifier,
        state: GetPointRecord,
    ) {
        Row(
            modifier.padding(vertical = 7.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = state.title,
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level1TextColor
                    )
                )
                val dateString by remember(state) {
                    derivedStateOf {
                        dateFormatter.format(Date(state.date))
                    }
                }
                Text(
                    text = dateString,
                    style = WanTheme.typography.h8.copy(
                        color = WanTheme.colors.level3TextColor
                    )
                )
            }
            Text(
                text = "+${state.points}",
                style = WanTheme.typography.h7.copy(
                    color = WanTheme.colors.tipColor
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPointRecordContent() {
    PointRecordContent(
        modifier = Modifier.fillMaxWidth(),
        state = PointScreen.GetPointRecord(
            id = 1,
            title = "签名积分12+2",
            date = System.currentTimeMillis(),
            points = 100
        )
    )
}

@Composable
@Preview(showBackground = true)
private fun PreviewPointContent() {
    val state = remember {
        PointScreen.PointState().apply {
            addRecord((0..100).map {
                PointScreen.GetPointRecord(
                    id = it,
                    title = "我是第${it}个",
                    date = System.currentTimeMillis() + it,
                    points = 100
                )
            })
        }
    }
    PointScreen.Content(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onClickBackButton = {},
        onLoadMore = {}
    )
}