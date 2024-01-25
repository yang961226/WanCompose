package com.sundayting.wancompose.page.setting

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.dialog.ConfirmDialog
import com.sundayting.wancompose.common.ui.dialog.ConfirmDialogTextStyle
import com.sundayting.wancompose.common.ui.loading.LoadingIndicator
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme

object SettingScreen : WanComposeDestination {
    override val route: String
        get() = "设置页"

    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: SettingViewModel = hiltViewModel(),
        navController: NavController = rememberNavController(),
    ) {

        var isShowLogoutDialog by remember { mutableStateOf(false) }
        if (isShowLogoutDialog) {
            ConfirmDialog(content = {
                Text(
                    text = stringResource(id = R.string.logout_desc),
                    style = ConfirmDialogTextStyle
                )
            }, onDismiss = {
                isShowLogoutDialog = false
            }, onConfirm = {
                viewModel.logout()
                isShowLogoutDialog = false
            })
        }

        BackHandler(enabled = viewModel.isLoading) {}
        TitleBarWithContent(
            modifier,
            titleBarContent = {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(radius = 25.dp, bounded = false),
                        ) { navController.popBackStack() }
                        .size(25.dp)
                        .align(Alignment.CenterStart),
                    colorFilter = ColorFilter.tint(TitleTextStyle.color),
                )
                Text(
                    stringResource(id = R.string.title_setting),
                    style = TitleTextStyle,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        ) {


            Column(
                Modifier
                    .matchParentSize()
                    .background(WanTheme.colors.level1BackgroundColor)
                    .verticalScroll(rememberScrollState())
            ) {
                NormalSettingLine(
                    title = stringResource(id = R.string.logout),
                    onClick = {
                        isShowLogoutDialog = true
                    }
                )
            }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.Center),
                visible = viewModel.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoadingIndicator(
                    Modifier.size(30.dp)
                )
            }
        }
    }

    @Composable
    private fun NormalSettingLine(
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        title: String,
    ) {

        Row(
            modifier
                .clickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                ) { onClick?.invoke() }
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = WanTheme.typography.h7.copy(
                    color = WanTheme.colors.level1TextColor
                )
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(1f, false)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                colorFilter = ColorFilter.tint(WanTheme.colors.level1TextColor)
            )
        }

    }

    fun NavController.navigateToSettingScreen() {
        navigate(
            route = route
        ) {
            launchSingleTop = true
        }
    }
}