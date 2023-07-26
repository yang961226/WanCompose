package com.sundayting.wancompose.homescreen.article.repo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.resources.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomePageServiceImpl @Inject constructor(
    private val client: HttpClient,
) : HomePageService {

    @Resource("/article")
    private class Article {
        @Resource("list")
        class List(val parent: Article = Article()) {

            @Resource("{id}")
            class Id(val parent: List = List(), val id: Int) {
                @Resource("json")
                class Json(val parent: Id)
            }

        }
    }

    override suspend fun fetchHomePageArticle(page: Int): HomePageArticleBean {
        return client.get(
            Article.List.Id.Json(
                Article.List.Id(
                    Article.List(), page
                )
            )
        ).body()
    }

    @Resource("/banner")
    class Banner {
        @Resource("json")
        class Json(val parent: Banner = Banner())
    }

    override suspend fun fetchHomePageBanner(): HomePageBannerBean {
        return client.get(
            Banner.Json(Banner())
        ).body()
    }
}