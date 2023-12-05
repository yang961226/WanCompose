package com.sundayting.wancompose.common.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sundayting.wancompose.R
import com.sundayting.wancompose.theme.WanColors

@Composable
fun ConfirmDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {

    Dialog(onDismissRequest = onDismiss) {
        ConfirmDialogContent(
            content = content,
            onConfirm = { onConfirm() },
            onCancel = onDismiss
        )
    }

}

@Composable
fun ConfirmDialogContent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
) {

    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
        Divider()
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f, false)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel),
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = Color.Gray.copy(0.8f)
                    )
                )
            }
            Divider(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f, false)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) { onConfirm() }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_confirm),
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = WanColors.TopColor
                    )
                )
            }
        }

    }

}

@Composable
@Preview
private fun PreviewConfirmDialog() {
    ConfirmDialogContent(Modifier.fillMaxWidth(), content = {
        Text("确定要退出登录吗")
    })
}