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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sundayting.wancompose.R
import com.sundayting.wancompose.theme.WanTheme

val ConfirmDialogTextStyle
    @Composable
    @ReadOnlyComposable
    get() = WanTheme.typography.h7.copy(
        color = WanTheme.colors.level1TextColor
    )

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
fun NormalConfirmDialog(
    mainContent: String,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit,
) {
    ConfirmDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm
    ) {
        Text(
            text = mainContent,
            style = ConfirmDialogTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
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
            .background(WanTheme.colors.level1BackgroundColor)
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
        Divider(color = WanTheme.colors.level4BackgroundColor)
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
                        indication = ripple()
                    ) { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_cancel),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level2TextColor
                    )
                )
            }
            Divider(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = WanTheme.colors.level4BackgroundColor
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f, false)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onConfirm() }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.dialog_confirm),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level1TextColor,
                        fontWeight = FontWeight.SemiBold
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
        Text("确定要退出登录吗", style = ConfirmDialogTextStyle)
    })
}