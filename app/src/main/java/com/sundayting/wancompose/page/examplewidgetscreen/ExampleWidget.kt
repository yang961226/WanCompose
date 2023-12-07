package com.sundayting.wancompose.page.examplewidgetscreen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.HomeScreen

class ExampleCardBean(
    val name: String,
    @DrawableRes val resId: Int,
)

@Composable
private fun ExampleCardItem(
    modifier: Modifier = Modifier,
    bean: ExampleCardBean,
    onClick: (ExampleCardBean) -> Unit = {},
) {

    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, color = Color.Gray.copy(0.7f), shape = RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            ) { onClick(bean) }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = bean.resId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Spacer(Modifier.height(10.dp))
        Text(bean.name, style = TextStyle(fontSize = 20.sp, color = Color.Black))
    }

}

object ExampleWidget : HomeScreen.HomeScreenPage {
    override val route: String
        get() = "代码案例"

    private val exampleList = listOf(
        ExampleCardBean(
            name = "探探滑卡",
            resId = R.drawable.ic_tantan_preview
        ),
        ExampleCardBean(
            name = "手势",
            resId = R.drawable.icon_point_input
        )
    )

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        onClick: (ExampleCardBean) -> Unit,
    ) {
        TitleBarWithContent(
            modifier,
            titleBarContent = {
                Text(
                    stringResource(id = R.string.bottom_tab_example), style = TextStyle(
                        fontSize = 16.sp, color = Color.White
                    ), modifier = Modifier.align(Alignment.Center)
                )
            }
        ) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 20.dp),
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp,
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                items(exampleList) {
                    ExampleCardItem(Modifier.fillMaxWidth(), it, onClick = onClick)
                }
            }
        }

    }
}