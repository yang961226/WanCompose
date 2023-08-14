package com.sundayting.wancompose.page.homescreen.article


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class ArticleBean(
    @PrimaryKey
    val id: Long,
    val title: String,
    val niceDate: String,
    val fresh: Boolean,
    val shareUser: String,
    val author: String,
    val chapterName: String,
    val superChapterName: String,
    val link: String,
    val collect: Boolean,
)

@Serializable
data class ArticleListBean(
    val curPage: Int,
    @SerialName("datas")
    val list: List<ArticleBean>,
)

fun ArticleBean.toArticleUiBean(
    isStick: Boolean = false,
): ArticleList.ArticleUiBean {
    return ArticleList.ArticleUiBean(
        title = title,
        date = niceDate,
        isStick = isStick,
        isNew = fresh,
        chapter = ArticleList.ArticleUiBean.Chapter(
            chapterName = chapterName,
            superChapterName = superChapterName,
        ),
        authorOrSharedUser = ArticleList.ArticleUiBean.AuthorOrSharedUser(
            author = author,
            sharedUser = shareUser
        ),
        id = id,
        link = link,
        isCollect = collect
    )
}

@Serializable
class ArticleResultBean(
    override val data: ArticleListBean?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<ArticleListBean>()