package com.sundayting.wancompose.page.examplewidgetscreen.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sundayting.wancompose.WanComposeDestination

class RedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return object : AbstractComposeView(inflater.context) {

            init {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            }

            @Composable
            override fun Content() {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Red.copy(0.2f))
                ) {
                    val pagerState = rememberPagerState { 2 }
                    HorizontalPager(pagerState) {
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
                        .fillMaxSize()
                        .background(Color.Blue.copy(0.2f))
                )
            }
        }
    }
}

object ViewPagerHorizontalPagerNestScroll : WanComposeDestination {
    override val route: String
        get() = "ViewPager横向滑动测试"

    fun NavController.navigateToViewPagerHorizontalPagerNestScroll() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
    ) {

        val fragmentActivity = (LocalContext.current as FragmentActivity)

        val viewPagerAdapter = remember(fragmentActivity) {
            object : FragmentStateAdapter(fragmentActivity) {
                override fun getItemCount(): Int = 2

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> RedFragment()
                        else -> BlueFragment()
                    }
                }

            }
        }

        AndroidView(modifier = modifier, factory = {
            ViewPager2(it).apply {
                id = View.generateViewId()
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                adapter = viewPagerAdapter
            }
        })

    }
}