package com.sundayting.wancompose.page.examplewidgetscreen.annonated

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

private const val testHtmlString =
    """测试文案测试文<mention uid="1104"></mention>案测试<avatar url="1234"></avatar>文案"""

@Composable
@Preview(showBackground = true)
fun AnnotatedStringExample() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White), contentAlignment = Alignment.Center
    ) {
        Text(
            buildAnnotatedString {
                parseNodes(Jsoup.parse(testHtmlString).body().childNodes())
            },
            style = TextStyle(
                fontSize = 30.sp
            ),
            inlineContent = mapOf(
                "avatar" to InlineTextContent(
                    placeholder = Placeholder(
                        width = 30.sp,
                        30.sp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                    )
                ) {
                    Box(
                        Modifier
                            .background(Color.Red, CircleShape)
                            .size(30.dp)
                    )
                }
            )
        )
    }
}

private fun AnnotatedString.Builder.parseNodes(
    nodes: List<Node>,
) {
    for (node in nodes) {
        when (node) {
            is TextNode -> append(node.text())
            is Element -> {
                when (node.tagName()) {
                    "mention" -> {
                        val uid = node.attr("uid")
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "",
                                linkInteractionListener = null,
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = Color.Red
                                    )
                                )
                            )
                        ) {
                            append(" @$uid ")
                        }
                    }

                    "avatar" -> {
                        val url = node.attr("url")
                        appendInlineContent("avatar", alternateText = url)
                    }
                }
            }

            else -> parseNodes(node.childNodes())
        }
    }
}