package com.sundayting.wancompose.page.homescreen.article

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList

/**
 * 文章页状态
 */
class ArticlePageState {

    var page = 0

    var isOpenBanner by mutableStateOf(true)
    var isShowLoadingBox by mutableStateOf(true)

    private val _articleList = mutableStateListOf<ArticleList.ArticleUiBean>()
    val articleList: List<ArticleList.ArticleUiBean> = _articleList

    fun changeArticle(index: Int, article: ArticleList.ArticleUiBean) {
        _articleList[index] = article
    }

    fun addArticle(list: List<ArticleList.ArticleUiBean>, clearFirst: Boolean = false) {
        if (clearFirst) {
            _articleList.clear()
        }
        _articleList.addAll(list)
    }

    val bannerList = mutableStateListOf<ArticleList.BannerUiBean>()

    var refreshing by mutableStateOf(false)
    var loadingMore by mutableStateOf(false)

}