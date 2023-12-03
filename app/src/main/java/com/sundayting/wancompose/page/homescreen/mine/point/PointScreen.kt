package com.sundayting.wancompose.page.homescreen.mine.point

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBarProperties
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.theme.WanColors

object PointScreen : WanComposeDestination {

    fun NavController.navigationToPointScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    override val route: String
        get() = "积分页面"

    @Composable
    fun Screen(
        modifier: Modifier,
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
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(WanColors.TopColor)
                ) {

                }
            }
        }
    }
}