package com.sundayting.wancompose.page.setting

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.sundayting.wancompose.LocalLoginUser
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.helper.LocalDarkMode
import com.sundayting.wancompose.common.helper.LocalDarkModeFollowSystem
import com.sundayting.wancompose.common.helper.LocalDarkModeHelper
import com.sundayting.wancompose.common.helper.LocalSetToDarkMode
import com.sundayting.wancompose.common.ui.dialog.ConfirmDialog
import com.sundayting.wancompose.common.ui.dialog.ConfirmDialogTextStyle
import com.sundayting.wancompose.common.ui.loading.LoadingIndicator
import com.sundayting.wancompose.common.ui.switchbutton.SwitchButton
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme

object SettingScreen : WanComposeDestination {

    override val route: String = "设置页"

    @Composable
    private fun SettingSpacer() {
        Spacer(Modifier.height(10.dp))
    }

    @Composable
    private fun Divider() {
        Box(
            Modifier
                .background(WanTheme.colors.level3BackgroundColor)
                .padding(start = 15.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(WanTheme.colors.level4BackgroundColor)
        )
    }

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

        val darkModeHelper = LocalDarkModeHelper.current

        val isDarkMode = LocalDarkMode.current
        val isDarkModeFollowSystem = LocalDarkModeFollowSystem.current
        val isSetDarkMode = LocalSetToDarkMode.current

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
                            indication = ripple(radius = 25.dp, bounded = false),
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
                modifier = Modifier
                    .matchParentSize()
                    .background(WanTheme.colors.level2BackgroundColor)
                    .verticalScroll(rememberScrollState()),
            ) {
                SettingSpacer()

                SwitchSettingLine(
                    title = stringResource(id = R.string.dark_mode_follow_system),
                    onClick = {
                        darkModeHelper.changeDarkModeFollowSystem(
                            originModeIsDark = isDarkMode,
                            tryFollow = !isDarkModeFollowSystem
                        )
                    },
                    isSelected = isDarkModeFollowSystem
                )

                SettingSpacer()

                val openBanner by viewModel.openBannerFlow.collectAsState(initial = true)

                SwitchSettingLine(
                    title = stringResource(id = R.string.show_banner),
                    onClick = {
                        viewModel.changedOpenBanner()
                    },
                    isSelected = openBanner
                )

                if (!isDarkModeFollowSystem) {
                    Divider()
                    SwitchSettingLine(
                        title = stringResource(id = R.string.dark_mode),
                        onClick = {
                            darkModeHelper.changeDarkModeSetting(
                                !isSetDarkMode
                            )
                        },
                        isSelected = isSetDarkMode
                    )
                }

                if (LocalLoginUser.current != null) {
                    SettingSpacer()

                    NormalSettingLine(
                        title = stringResource(id = R.string.logout),
                        onClick = {
                            isShowLogoutDialog = true
                        }
                    )
                }
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
                .background(WanTheme.colors.level3BackgroundColor)
                .clickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
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

    @Composable
    private fun SwitchSettingLine(
        modifier: Modifier = Modifier,
        onClick: ((Boolean) -> Unit)? = null,
        isSelected: Boolean = false,
        title: String,
    ) {

        Row(
            modifier
                .background(WanTheme.colors.level3BackgroundColor)
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

            SwitchButton(
                modifier = Modifier.size(38.5.dp, 22.dp),
                isSelected = isSelected,
                onClick = { onClick?.invoke(it) }
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