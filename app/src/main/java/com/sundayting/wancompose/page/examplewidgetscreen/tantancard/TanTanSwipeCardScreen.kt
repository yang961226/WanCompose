package com.sundayting.wancompose.page.examplewidgetscreen.tantancard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.examplewidgetscreen.ExampleCardBean

object TanTanSwipeCardScreen : WanComposeDestination {
    override val route: String
        get() = exampleCardBean.name

    val exampleCardBean = ExampleCardBean(
        name = "探探滑卡",
        resId = R.drawable.ic_tantan_preview
    )

    fun NavController.navigateToTanTanSwipeCardScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(modifier: Modifier = Modifier) {
        TitleBarWithContent(
            modifier,
            titleBarContent = {
                Text(
                    "探探滑卡", style = TextStyle(
                        fontSize = 16.sp, color = Color.White
                    ), modifier = Modifier.align(Alignment.Center)
                )
            }
        ) {
            val list = remember {
                mutableStateListOf<TanTanUserBean>().apply {
                    addAll(TestExample.userList)
                }
            }
            TanTanSwipeCard(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(vertical = 40.dp, horizontal = 20.dp),
                userList = list.asReversed(),
                onSwipeToDismiss = {
                    list.removeFirst()
                    list.add(TestExample.getNextUser())
                }
            )
        }
    }

}