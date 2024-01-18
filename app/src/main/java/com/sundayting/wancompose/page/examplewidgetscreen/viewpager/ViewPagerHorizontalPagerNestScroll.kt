package com.sundayting.wancompose.page.examplewidgetscreen.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.NavController
import androidx.viewpager.widget.ViewPager
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBarProperties
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.examplewidgetscreen.ExampleCardBean
import kotlinx.coroutines.launch

class RedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return object : AbstractComposeView(inflater.context) {

            private var canScrollForward = true
            private var canScrollBackward = true
            private var lastX = 0f
            private var lastY = 0f

            override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
                when (ev.actionMasked) {
                    ACTION_DOWN -> {
                        lastX = ev.x
                        lastY = ev.y
                    }

                    ACTION_MOVE -> {
                        val dx = ev.x - lastX
                        val needParentScroll =
                            (dx < 0 && !canScrollForward) || (dx >= 0 && !canScrollBackward)
                        if (needParentScroll) {
                            return true
                        }
                    }
                }
                return super.onInterceptTouchEvent(ev)
            }

//            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//                when (ev.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        lastX = ev.x
//                        lastY = ev.y
//                    }
//
//                    MotionEvent.ACTION_MOVE -> {
//                        val dx = ev.x - lastX
//                        val dy = ev.y - lastY
//                        lastX = ev.x
//                        lastY = ev.y
//
//                        val r = abs(dy) / abs(dx)
//                        val needParentScroll =
//                            (dx < 0 && !canScrollForward) || (dx >= 0 && !canScrollBackward)
//
//                        if (r < 0.6f && needParentScroll) {
//                            return false
//                        }
//                    }
//
//                }
//                return super.dispatchTouchEvent(ev)
//            }

            init {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            }

            @Composable
            override fun Content() {

                val scrollState = rememberScrollableState {
                    it
                }
                var canScrollForward2 by remember { mutableStateOf(true) }
                var canScrollBackward2 by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    launch {
                        snapshotFlow { scrollState.canScrollForward }.collect {
                            canScrollForward2 = it
                        }
                    }
                    launch {
                        snapshotFlow { scrollState.canScrollBackward }.collect {
                            canScrollBackward2 = it
                        }
                    }
                }
                Box(
                    Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color.Red, RoundedCornerShape(10.dp))
                        .background(Color.Red.copy(0.2f))
                ) {
                    val pagerState = rememberPagerState { 2 }
                    LaunchedEffect(Unit) {
                        launch {
                            snapshotFlow { pagerState.canScrollForward }.collect {
                                canScrollForward = it
                            }
                        }
                        launch {
                            snapshotFlow { pagerState.canScrollBackward }.collect {
                                canScrollBackward = it
                            }
                        }
                        launch {
                            snapshotFlow { pagerState }.collect {

                            }
                        }
                    }
                    Box(contentAlignment = Alignment.Center) {
                        HorizontalPager(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.dp,
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.Black
                                ),
                            state = pagerState
                        ) {
                            when (it) {
                                0 -> Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.5f)
                                        .background(Color.Yellow)
                                )

                                else -> Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.5f)
                                        .background(Color.Magenta)
                                )
                            }
                        }

                        Text("我是HorizontalPager", style = TextStyle(fontSize = 30.sp))
                    }

                }
            }

        }
    }

}

class BlueFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(inflater.context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Box(
                    Modifier
                        .statusBarsPadding()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxSize()
                        .border(1.dp, Color.Blue, RoundedCornerShape(10.dp))
                        .background(Color.Blue.copy(0.2f))
                )
            }
        }
    }
}

object ViewPagerHorizontalPagerNestScroll : WanComposeDestination {
    override val route: String
        get() = "ViewPager横向滑动测试"

    val exampleCardBean = ExampleCardBean(
        "ViewPager、HorizontalPager嵌套滑动问题修复",
        resId = R.drawable.ic_horizontalpager_issues
    )

    fun NavController.navigateToViewPagerHorizontalPagerNestScroll() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        onClickBackButton: () -> Unit = {},
    ) {

        val fragmentActivity = (LocalContext.current as FragmentActivity)

        val viewPagerAdapter = remember(fragmentActivity) {
            object : FragmentStatePagerAdapter(fragmentActivity.supportFragmentManager) {
                override fun getCount(): Int = 2

                override fun getItem(position: Int): Fragment {
                    return when (position) {
                        0 -> RedFragment()
                        else -> BlueFragment()
                    }
                }

            }
        }

        TitleBarWithContent(
            modifier,
            properties = TitleBarProperties(elevation = 0.dp),
            titleBarContent = {
                TitleBarWithBackButtonContent(
                    onClickBackButton = onClickBackButton
                ) {
                    Text(
                        "嵌套滑动",
                        style = TextStyle(
                            fontSize = 16.sp, color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        ) {
            Box(
                Modifier
                    .fillMaxSize()
            ) {
                AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                    ViewPager(it).apply {
                        id = View.generateViewId()
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        adapter = viewPagerAdapter
                    }
                })
                Text(
                    text = "我是ViewPager",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 25.dp),
                    style = TextStyle(fontSize = 30.sp)
                )
            }
        }
    }
}