package com.sundayting.wancompose.page.homescreen.mine.point

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBarProperties
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.mine.point.PointScreen.PointRecordContent
import com.sundayting.wancompose.theme.WanColors
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

object PointScreen : WanComposeDestination {

    fun NavController.navigationToPointScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    override val route: String
        get() = "积分页面"

    @HiltViewModel
    class MyPointViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {

        companion object {

            const val TOTAL_POINT_KEY = "总积分"

        }

        val state = PointState(savedStateHandle[TOTAL_POINT_KEY] ?: 0)
    }

    @Stable
    class PointState(
        val point: Int,
    ) {
        private val _pointRecordList = mutableStateListOf<GetPointRecord>()
        val pointRecordList: List<GetPointRecord> = _pointRecordList

        fun addRecord(list: List<GetPointRecord>) {
            _pointRecordList.addAll(list)
        }

        var isLoading by mutableStateOf(false)
    }

    data class GetPointRecord(
        val title: String,
        val date: Long,
        val points: Int,
    )

    @Composable
    fun Content(
        modifier: Modifier = Modifier,
        state: PointState,
        onClickBackButton: () -> Unit,
    ) {
        TitleBarWithContent(
            modifier,
            properties = TitleBarProperties(elevation = 0.dp),
            titleBarContent = {
                TitleBarWithBackButtonContent(
                    onClickBackButton = onClickBackButton
                ) {
                    Text(
                        stringResource(id = R.string.my_points),
                        style = TextStyle(
                            fontSize = 16.sp, color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                val number = remember { Animatable(0f) }
                LaunchedEffect(state) {
                    number.animateTo(state.point.toFloat(), animationSpec = tween(1000))
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(WanColors.TopColor)
                ) {
                    Text(
                        text = number.value.toInt().toString(),
                        style = TextStyle(fontSize = 70.sp, color = Color.White),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                LazyColumn(
                    modifier
                        .fillMaxSize()
                        .weight(1f, false),
                ) {
                    items(state.pointRecordList, key = { it.date }, contentType = { 1 }) { record ->
                        PointRecordContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            state = record
                        )
                        Divider(Modifier.fillMaxWidth())
                    }
                    if (state.isLoading) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
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
        viewModel: MyPointViewModel = hiltViewModel(),
        onClickBackButton: () -> Unit,
    ) {
        Content(
            modifier,
            state = viewModel.state,
            onClickBackButton = onClickBackButton
        )
    }


    private val mainTitleColor = Color.Black.copy(0.8f)
    private val dateColor = Color.Black.copy(0.6f)
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
                    style = TextStyle(fontSize = 15.sp, color = mainTitleColor)
                )
                val dateString by remember(state) {
                    derivedStateOf {
                        dateFormatter.format(Date(state.date))
                    }
                }
                Text(
                    text = dateString,
                    style = TextStyle(fontSize = 11.sp, color = dateColor)
                )
            }
            Text(
                text = "+${state.points}",
                style = TextStyle(fontSize = 16.sp, color = WanColors.TopColor)
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
        PointScreen.PointState(
            point = 52
        ).apply {
            addRecord((0..100).map {
                PointScreen.GetPointRecord(
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
        onClickBackButton = {}
    )
}