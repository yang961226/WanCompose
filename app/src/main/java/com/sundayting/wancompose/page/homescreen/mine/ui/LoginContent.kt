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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanViewModel
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.theme.WanColors
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
            .background(Color.White)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(10f / 7f), onDraw = {
                val path = Path()
                path.apply {
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
                drawPath(path, Color(0xFF5280EC))
            })
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
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = stringResource(id = R.string.login_title_2),
                style = TextStyle(
                    color = Color.White.copy(0.6f),
                    fontSize = 12.sp
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
                                    EventManager.emitToast("再次输入的密码不匹配！")
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
                indication = rememberRipple()
            ) { onToRegister() }
        ) {
            Text(
                text = stringResource(id = R.string.to_register),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = WanColors.TopColor
                )
            )
            Spacer(Modifier.width(5.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_direction_right),
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                colorFilter = ColorFilter.tint(WanColors.TopColor)
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
            label = { Text(stringResource(id = R.string.please_input_account)) },
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
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = WanColors.TopColor,
                focusedLabelColor = WanColors.TopColor,
                focusedBorderColor = WanColors.TopColor,
            ),
            shape = RoundedCornerShape(50),
        )

        OutlinedTextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = password,
            onValueChange = { password = it.removeEmptyAndNewLine() },
            singleLine = true,
            enabled = state.isLoading.not(),
            label = { Text(stringResource(id = R.string.please_input_password)) },
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
                    modifier = Modifier.size(20.dp)
                )
            },

            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = WanColors.TopColor,
                focusedLabelColor = WanColors.TopColor,
                focusedBorderColor = WanColors.TopColor,
            ),
            shape = RoundedCornerShape(50)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            enabled = inputFinished && state.isLoading.not(),
            onClick = {
                onClickConfirm(username, password)
            }, colors = ButtonDefaults.buttonColors(
                backgroundColor = WanColors.TopColor
            ),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 100.dp, vertical = 10.dp)
        ) {
            Text(
                stringResource(id = R.string.to_login), style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                )
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
                indication = rememberRipple()
            ) { onToLogin() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_direction_left),
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                colorFilter = ColorFilter.tint(WanColors.TopColor)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = stringResource(id = R.string.to_login),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = WanColors.TopColor
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
            label = { Text(stringResource(id = R.string.please_input_account)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_account_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = WanColors.TopColor,
                focusedLabelColor = WanColors.TopColor,
                focusedBorderColor = WanColors.TopColor,
            ),
            shape = RoundedCornerShape(50)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it.removeEmptyAndNewLine() },
            singleLine = true,
            enabled = state.isLoading.not(),
            label = { Text(stringResource(id = R.string.please_input_password)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },

            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = WanColors.TopColor,
                focusedLabelColor = WanColors.TopColor,
                focusedBorderColor = WanColors.TopColor,
            ),
            shape = RoundedCornerShape(50)
        )

        OutlinedTextField(
            value = passwordAgain,
            onValueChange = { passwordAgain = it.removeEmptyAndNewLine() },
            singleLine = true,
            enabled = state.isLoading.not(),
            label = { Text(stringResource(id = R.string.please_input_password_again)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },

            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = WanColors.TopColor,
                focusedLabelColor = WanColors.TopColor,
                focusedBorderColor = WanColors.TopColor,
            ),
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
                backgroundColor = WanColors.TopColor
            ),
            enabled = buttonEnable && state.isLoading.not(),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 100.dp, vertical = 10.dp)
        ) {
            Text(
                stringResource(id = if (state.isLoading) R.string.waiting else R.string.to_register),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                )
            )
        }
    }
}

@Composable
@Preview
private fun PreviewLoginContent() {
    LoginContent(
        loginOrRegisterState = WanViewModel.LoginOrRegisterState()
    )
}