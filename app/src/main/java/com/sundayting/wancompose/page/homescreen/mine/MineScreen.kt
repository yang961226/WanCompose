package com.sundayting.wancompose.page.homescreen.mine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.function.UserLoginFunction.UserEntity
import com.sundayting.wancompose.theme.WanColors

object MineScreen : WanComposeDestination {

    override val route: String
        get() = "个人页"

    @Composable
    fun Screen(modifier: Modifier, userEntity: UserEntity) {
        val scrollState = rememberScrollState()
        Column(
            modifier
                .verticalScroll(scrollState)
                .background(Color.White)
        ) {
            ConstraintLayout(
                Modifier
                    .fillMaxWidth()
                    .background(WanColors.TopColor)
                    .padding(vertical = 30.dp)
            ) {
                val (
                    headContent,
                    nickContent,
                    levelContent,
                ) = createRefs()


                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .constrainAs(headContent) {
                            centerHorizontallyTo(parent)
                        },
                    painter = painterResource(id = R.drawable.ic_login_icon),
                    contentDescription = null
                )

                Text(
                    text = userEntity.nick,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.constrainAs(nickContent) {
                        centerHorizontallyTo(parent)
                        top.linkTo(headContent.bottom, 15.dp)
                    }
                )

                Row(
                    Modifier.constrainAs(levelContent) {
                        centerHorizontallyTo(parent)
                        top.linkTo(nickContent.bottom, 5.dp)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.level_d, userEntity.level),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White.copy(0.8f)
                        )
                    )

                    Spacer(Modifier.width(5.dp))

                    Text(
                        text = stringResource(id = R.string.rank_d, userEntity.rank),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White.copy(0.8f)
                        )
                    )
                }
            }
        }
    }

}

@Composable
@Preview
private fun PreviewMineScreen() {
    MineScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        remember {
            UserEntity(
                id = 1104,
                nick = "我是名字",
                coinCount = 0,
                level = 0,
                rank = 1000
            )
        }
    )
}