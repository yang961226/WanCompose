package com.sundayting.wancompose.page.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.textfield.WanTextField
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.share.ShareScreen.ShareContent
import com.sundayting.wancompose.theme.AlwaysDarkModeArea
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme

object ShareScreen : WanComposeDestination {

    override val route: String = "分享页面"

    fun NavController.navigateToShareScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        viewModel: ShareViewModel = hiltViewModel(),
        navController: NavController = rememberNavController(),
    ) {
        ShareContent(
            modifier,
            state = viewModel.state,
            onClickBack = {
                navController.popBackStack()
            },
            onLinkInputChanged = viewModel::onLinkInputChanged,
            onTitleInputChanged = viewModel::onTitleInputChanged,
            onClickShare = viewModel::shareArticle
        )
    }

    @Composable
    fun ShareContent(
        modifier: Modifier = Modifier,
        state: ShareViewModel.ShareUiState,
        onTitleInputChanged: (String) -> Unit = {},
        onLinkInputChanged: (String) -> Unit = {},
        onClickBack: () -> Unit,
        onClickShare: () -> Unit,
    ) {

        TitleBarWithContent(
            modifier = modifier,
            titleBarContent = {
                TitleBarWithBackButtonContent(onClickBackButton = onClickBack) {
                    Text(
                        text = stringResource(id = R.string.share_article),
                        style = TitleTextStyle,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) {

            Column(
                Modifier
                    .fillMaxSize()
                    .background(WanTheme.colors.level1BackgroundColor)
                    .padding(15.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.article_title),
                    style = WanTheme.typography.h8.copy(
                        color = WanTheme.colors.level2TextColor
                    )
                )

                Spacer(Modifier.height(10.dp))

                WanTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.titleInput,
                    onValueChange = onTitleInputChanged,
                    placeHolder = {
                        Text(
                            text = stringResource(id = R.string.share_title_hint),
                            style = WanTheme.typography.h7.copy(
                                color = WanTheme.colors.level3TextColor
                            ),
                        )
                    },
                    textStyle = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level1TextColor
                    )
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = stringResource(id = R.string.article_link),
                    style = WanTheme.typography.h8.copy(
                        color = WanTheme.colors.level2TextColor
                    )
                )

                Spacer(Modifier.height(10.dp))

                WanTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.linkInput,
                    onValueChange = onLinkInputChanged,
                    placeHolder = {
                        Text(
                            text = stringResource(id = R.string.share_link_hint),
                            style = WanTheme.typography.h7.copy(
                                color = WanTheme.colors.level3TextColor
                            )
                        )
                    },
                    textStyle = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level1TextColor
                    )
                )


                Spacer(Modifier.height(50.dp))

                val canShare by remember(state) {
                    derivedStateOf {
                        state.linkInput.isNotEmpty() && state.titleInput.isNotEmpty()
                    }
                }

                AlwaysDarkModeArea {
                    Text(
                        text = stringResource(id = R.string.share),
                        style = WanTheme.typography.h7.copy(
                            color = WanTheme.colors.level1TextColor
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                WanTheme.colors.primaryColor.copy(
                                    if (canShare) 1f else 0.7f
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(),
                                enabled = canShare
                            ) {
                                onClickShare()
                            }
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .padding(vertical = 10.dp)
                    )
                }

                Spacer(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f, false)
                )

                Text(
                    text = stringResource(id = R.string.share_tips),
                    style = WanTheme.typography.h8.copy(
                        color = WanTheme.colors.level3TextColor
                    )
                )


            }

        }

    }
}

@Composable
@Preview
private fun PreviewShareContent() {
    val state = remember {
        ShareViewModel.ShareUiState()
    }
    ShareContent(
        Modifier.fillMaxSize(),
        onClickBack = {},
        state = state,
        onTitleInputChanged = {

        },
        onLinkInputChanged = {

        },
        onClickShare = {

        }
    )
}