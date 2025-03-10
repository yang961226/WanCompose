package com.sundayting.wancompose.page.homescreen.mine.ui

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanViewModel
import com.sundayting.wancompose.common.event.LocalEventManager
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.common.helper.LocalDarkMode
import com.sundayting.wancompose.theme.WanTheme
import kotlinx.coroutines.launch

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    loginOrRegisterState: WanViewModel.LoginOrRegisterState,
    pagerState: PagerState = rememberPagerState { 2 },
    onClickLogin: (username: String, password: String) -> Unit = { _, _ -> },
    onClickRegister: (username: String, password: String, passwordAgain: String) -> Unit = { _, _, _ -> },
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(WanTheme.colors.level4BackgroundColor)
    ) {
        val primaryColor = WanTheme.colors.primaryColor
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(10f / 7f),
            onDraw = {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    cubicTo(
                        size.width,
                        size.height,
                        size.width / 2f,
                        size.height / 2,
                        0f,
                        size.height
                    )
                    close()
                }
                drawPath(path, primaryColor)
            }
        )
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
            )

            Image(
                painter = painterResource(id = R.drawable.ic_login_icon),
                contentDescription = null,
                modifier = Modifier
                    .composed {
                        val rotateTransition = rememberInfiniteTransition(label = "")
                        val degree by rotateTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = if (loginOrRegisterState.isLoading) 360f else 0f,
                            animationSpec = InfiniteRepeatableSpec(
                                animation = tween(durationMillis = 2500),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = ""
                        )
                        Modifier.rotate(degree)
                    }
                    .size(120.dp)
            )
            Text(
                text = stringResource(id = R.string.login_title_1),
                style = WanTheme.typography.h7.copy(
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = stringResource(id = R.string.login_title_2),
                style = WanTheme.typography.h8.copy(
                    color = Color.White.copy(0.7f)
                ),
            )


            val scope = rememberCoroutineScope()

            HorizontalPager(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth()
                    .height(400.dp),
                state = pagerState
            ) { page ->
                when (page) {
                    0 -> {
                        LoginPage(
                            Modifier.fillMaxSize(),
                            state = loginOrRegisterState,
                            onToRegister = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            onClickConfirm = { username, password ->
                                onClickLogin(username, password)
                            }
                        )
                    }

                    1 -> {

                        val eventManager = LocalEventManager.current
                        RegisterPage(
                            Modifier.fillMaxSize(),
                            state = loginOrRegisterState,
                            onToLogin = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            onClickConfirm = { username, password, passwordAgain ->
                                if (password != passwordAgain) {
                                    eventManager.emitToast(R.string.password_not_same)
                                } else {
                                    onClickRegister(username, password, passwordAgain)
                                }
                            }
                        )
                    }
                }
            }
        }


    }
}

private fun String.removeEmptyAndNewLine(): String {
    return replace("\n", "").replace(" ", "")
}

@Composable
fun textFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        cursorColor = WanTheme.colors.tipColor,
        focusedLabelColor = WanTheme.colors.primaryColor,
        focusedTextColor = WanTheme.colors.level1TextColor,
        unfocusedTextColor = WanTheme.colors.level3TextColor,
        disabledTextColor = WanTheme.colors.level3TextColor,
        focusedContainerColor = WanTheme.colors.level4BackgroundColor,
        unfocusedContainerColor = WanTheme.colors.level2BackgroundColor,
        disabledContainerColor = WanTheme.colors.level2BackgroundColor,
    )
}

@Composable
private fun LoginPage(
    modifier: Modifier = Modifier,
    state: WanViewModel.LoginOrRegisterState,
    onClickConfirm: (username: String, password: String) -> Unit = { _, _ -> },
    onToRegister: () -> Unit = {},
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onToRegister() }
        ) {
            Text(
                text = stringResource(id = R.string.to_register),
                style = WanTheme.typography.h6.copy(
                    color = WanTheme.colors.tipColor
                )
            )
            Spacer(Modifier.width(5.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_direction_right),
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                colorFilter = ColorFilter.tint(WanTheme.colors.tipColor)
            )
        }

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        val inputFinished by remember {
            derivedStateOf {
                username.isNotEmpty() && password.isNotEmpty()
            }
        }

        val focusRequester = remember { FocusRequester() }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequester),
            value = username,
            onValueChange = { username = it.removeEmptyAndNewLine() },
            singleLine = true,
            enabled = state.isLoading.not(),
            label = {
                Text(
                    stringResource(id = R.string.please_input_account),
                    style = WanTheme.typography.h7,
                    color = WanTheme.colors.level3TextColor
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequester.requestFocus()
                }
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = WanTheme.colors.level3TextColor
                )
            },
            colors = textFieldColors(),
            shape = RoundedCornerShape(50),
        )

        OutlinedTextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = password,
            onValueChange = { password = it.removeEmptyAndNewLine() },
            singleLine = true,
            enabled = state.isLoading.not(),
            label = {
                Text(
                    stringResource(id = R.string.please_input_password),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level3TextColor
                    )
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = if (inputFinished) ImeAction.Done else ImeAction.None
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onClickConfirm(username, password)
                }
            ),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = WanTheme.colors.level3TextColor
                )
            },

            colors = textFieldColors(),
            shape = RoundedCornerShape(50)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            enabled = inputFinished && state.isLoading.not(),
            onClick = {
                onClickConfirm(username, password)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = WanTheme.colors.primaryColor,
                disabledContainerColor = WanTheme.colors.level1BackgroundColor,
                contentColor = Color.White,
                disabledContentColor = WanTheme.colors.level3TextColor
            ),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 100.dp, vertical = 10.dp)
        ) {
            Text(
                stringResource(id = R.string.to_login),
                style = WanTheme.typography.h6,
            )
        }

    }
}


@Composable
private fun RegisterPage(
    modifier: Modifier = Modifier,
    state: WanViewModel.LoginOrRegisterState,
    onClickConfirm: (username: String, password: String, passwordAgain: String) -> Unit = { _, _, _ -> },
    onToLogin: () -> Unit = {},
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onToLogin() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_direction_left),
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                colorFilter = ColorFilter.tint(WanTheme.colors.tipColor)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = stringResource(id = R.string.to_login),
                style = WanTheme.typography.h6.copy(
                    color = WanTheme.colors.tipColor
                )
            )
        }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordAgain by remember { mutableStateOf("") }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it.removeEmptyAndNewLine() },
            singleLine = true,
            enabled = state.isLoading.not(),
            label = {
                Text(
                    stringResource(id = R.string.please_input_account),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level3TextColor
                    )
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = WanTheme.colors.level1TextColor.copy(0.7f)
                )
            },
            colors = textFieldColors(),
            shape = RoundedCornerShape(50)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it.removeEmptyAndNewLine() },
            singleLine = true,
            enabled = state.isLoading.not(),
            label = {
                Text(
                    stringResource(id = R.string.please_input_password),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level3TextColor
                    )
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = WanTheme.colors.level1TextColor.copy(0.7f)
                )
            },

            colors = textFieldColors(),
            shape = RoundedCornerShape(50)
        )

        OutlinedTextField(
            value = passwordAgain,
            onValueChange = { passwordAgain = it.removeEmptyAndNewLine() },
            singleLine = true,
            enabled = state.isLoading.not(),
            label = {
                Text(
                    stringResource(id = R.string.please_input_password_again),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level3TextColor
                    )
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = WanTheme.colors.level1TextColor.copy(0.7f)
                )
            },

            colors = textFieldColors(),
            shape = RoundedCornerShape(50)
        )

        Spacer(Modifier.height(20.dp))

        val buttonEnable by remember {
            derivedStateOf {
                username.isNotEmpty() && password.isNotEmpty() && passwordAgain.isNotEmpty()
            }
        }

        Button(
            onClick = {
                onClickConfirm(username, password, passwordAgain)
            }, colors = ButtonDefaults.buttonColors(
                containerColor = WanTheme.colors.primaryColor,
                disabledContainerColor = WanTheme.colors.level4BackgroundColor
            ),
            enabled = buttonEnable && state.isLoading.not(),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 100.dp, vertical = 10.dp)
        ) {
            Text(
                stringResource(id = if (state.isLoading) R.string.waiting else R.string.to_register),
                style = WanTheme.typography.h6,
                color = Color.White
            )
        }
    }
}

@Composable
@Preview
private fun PreviewLoginContent() {
    CompositionLocalProvider(LocalDarkMode provides true) {
        WanTheme {
            LoginContent(
                loginOrRegisterState = WanViewModel.LoginOrRegisterState()
            )
        }
    }

}

@Composable
@Preview
private fun PreviewLoginContent2() {
    CompositionLocalProvider(LocalDarkMode provides false) {
        WanTheme {
            LoginContent(
                loginOrRegisterState = WanViewModel.LoginOrRegisterState()
            )
        }
    }

}