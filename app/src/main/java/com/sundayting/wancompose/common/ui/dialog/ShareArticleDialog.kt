package com.sundayting.wancompose.common.ui.dialog

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.sundayting.wancompose.R
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.article.ui.getShareQrString
import com.sundayting.wancompose.theme.AlwaysLightModeArea
import com.sundayting.wancompose.theme.WanTheme
import kotlinx.coroutines.launch
import java.util.Hashtable

@Composable
fun ShareArticleDialog(
    modifier: Modifier = Modifier,
    articleUiBean: ArticleList.ArticleUiBean,
    onDismissRequest: () -> Unit,
    onClickSave: (ImageBitmap) -> Unit,
    onClickShareNow: (ImageBitmap) -> Unit,
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {

        val graphicsLayer = rememberGraphicsLayer()
        val scope = rememberCoroutineScope()

        ConstraintLayout(modifier.fillMaxSize()) {
            MainContent(
                modifier = Modifier
                    .constrainAs(createRef()) {
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent, 0.4f)
                    }
                    .drawWithCache {
                        onDrawWithContent {
                            graphicsLayer.record {
                                this@onDrawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                    },
                title = articleUiBean.title,
                qrString = remember(articleUiBean) {
                    articleUiBean.getShareQrString()
                }
            )

            Row(Modifier.constrainAs(createRef()) {
                bottom.linkTo(parent.bottom, 25.dp)
                centerHorizontallyTo(parent)
            }, horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                Button(
                    title = stringResource(id = R.string.save_pic),
                    iconId = R.drawable.ic_photo,
                    onClick = {
                        scope.launch {
                            onClickSave(graphicsLayer.toImageBitmap())
                        }
                    }
                )
                Button(
                    title = stringResource(id = R.string.share_now),
                    iconId = R.drawable.ic_share,
                    onClick = {
                        scope.launch {
                            onClickShareNow(graphicsLayer.toImageBitmap())
                        }
                    }
                )
            }
        }
    }

}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    title: String,
    qrString: String,
) {

    AlwaysLightModeArea {
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        val density = LocalDensity.current
        LaunchedEffect(qrString, density) {
            with(density) {
                val hints: Hashtable<EncodeHintType, String> = Hashtable()
                hints[EncodeHintType.MARGIN] = "0"
                val height = 50.dp.roundToPx()
                val width = 50.dp.roundToPx()
                val bitMatrix = QRCodeWriter().encode(
                    qrString,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                    hints
                )
                val pixels = IntArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        if (bitMatrix[x, y]) {
                            pixels[y * width + x] = android.graphics.Color.BLACK
                        } else {
                            pixels[y * width + x] = android.graphics.Color.WHITE
                        }
                    }
                }
                imageBitmap = createBitmap(width, height).apply {
                    setPixels(pixels, 0, width, 0, 0, width, height)
                }.asImageBitmap()
            }

        }
        Column(
            modifier
                .fillMaxWidth()
                .background(WanTheme.colors.level1BackgroundColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.wan_android_suffix, title),
                style = WanTheme.typography.h6.copy(
                    color = WanTheme.colors.level1TextColor
                ),
                modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = stringResource(id = R.string.wan_compose_desc),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level3TextColor
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f, false)
                )
                Spacer(Modifier.width(10.dp))
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(WanTheme.colors.level3BackgroundColor)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_login_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(20.dp)
                )
                Text(
                    text = stringResource(id = R.string.made_by_wan_compose),
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
private fun PreviewMainContent() {
    MainContent(title = "哈哈哈哈哈哈哈哈哈我是标题哈哈哈哈", qrString = "哈哈哈哈")
}

@Composable
private fun Button(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes iconId: Int,
    onClick: () -> Unit,
) {

    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(WanTheme.colors.level2BackgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) {
                onClick()
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(WanTheme.colors.level1TextColor)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            style = WanTheme.typography.h7.copy(
                color = WanTheme.colors.level1TextColor
            )
        )
    }

}

