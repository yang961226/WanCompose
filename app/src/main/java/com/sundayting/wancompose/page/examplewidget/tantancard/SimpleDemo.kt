package com.sundayting.wancompose.page.examplewidget.tantancard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class SimpleBean(
    val color: Color,
)

@Composable
fun SimpleDemo(
    modifier: Modifier = Modifier,
    beanList: List<SimpleBean>,
) {

    Box(modifier, contentAlignment = Alignment.Center) {
        beanList.forEachIndexed { index, simpleBean ->
            key(simpleBean) {
                val toLastDis = beanList.size - (index + 1)
                LaunchedEffect(Unit) {
                    Log.d("临时测试", "新建${simpleBean.hashCode()}")
                }
                Box(
                    Modifier
                        .offset(0.dp, (-30).dp * (toLastDis))
                        .fillMaxSize(1 - toLastDis * 0.05f)
                        .background(simpleBean.color)
                        .border(width = 1.dp, Color.Gray)
                ) {
                    Text(
                        text = simpleBean.hashCode().toString(),
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }

            }
        }
    }

}

val colorTemplate = listOf(Color.White, Color.Red, Color.Green, Color.Yellow)

@Composable
@Preview
fun PreviewSimpleDemo() {
    val list = remember {
        mutableStateListOf<SimpleBean>().apply {
            add(SimpleBean(Color.Red))
            add(SimpleBean(Color.Yellow))
            add(SimpleBean(Color.Blue))
        }
    }
    Column(Modifier.padding(start = 100.dp)) {
        Button(onClick = {
            list.removeFirst()
            list.add(SimpleBean(colorTemplate[Random.nextInt(colorTemplate.size)]))
        }) {
            Text("点击添加")
        }

        Spacer(Modifier.height(200.dp))
        SimpleDemo(Modifier.size(200.dp), beanList = list)
    }

}

@Composable
@Preview
fun ScaleTest() {
    val scale = 0.9f
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(Modifier.size(200.dp)) {
            Box(
                Modifier
                    .size(200.dp)
                    .background(Color.Red)
            )
            Box(
                Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationY = -maxHeight.toPx() * (1 - scale) / 2
                    }
                    .background(Color.Gray)
            )
        }
    }

}