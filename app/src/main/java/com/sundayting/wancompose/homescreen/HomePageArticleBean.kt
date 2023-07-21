package com.sundayting.wancompose.homescreen

import com.sundayting.wancompose.network.WanNetResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ArticleBean(
    val title: String,
    val niceDate: String,
)

@Serializable
data class ArticleResultBean(
    val curPage: Int,
    @SerialName("datas")
    val list: List<ArticleBean>,
)

@Serializable
data class HomePageArticleBean(
    override val data: ArticleResultBean?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNetResult<ArticleResultBean>()