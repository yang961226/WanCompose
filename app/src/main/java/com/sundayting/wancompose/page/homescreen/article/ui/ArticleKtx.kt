package com.sundayting.wancompose.page.homescreen.article.ui

/**
 * 获取分享二维码的文本
 */
fun ArticleList.ArticleUiBean.getShareQrString(): String {
    return """玩Compose分享文章：{"title":${title},"id":${id}},"link":${link}"""
}