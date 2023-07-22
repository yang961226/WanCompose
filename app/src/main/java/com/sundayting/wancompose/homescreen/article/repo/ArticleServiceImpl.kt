package com.sundayting.wancompose.homescreen.article.repo

import com.sundayting.wancompose.homescreen.HomeScreenViewModel
import com.sundayting.wancompose.network.Ktor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import javax.inject.Inject
import javax.inject.Singleton

class ArticleServiceImpl @Inject constructor() : ArticleService {
    override suspend fun fetchHomePageArticle(page: Int): HomePageArticleBean {
        return Ktor.client.get(
            HomeScreenViewModel.Article.List.Id.Json(
                HomeScreenViewModel.Article.List.Id(
                    HomeScreenViewModel.Article.List(), page
                )
            )
        ).body()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ArticleServiceModule {

    @Singleton
    @Binds
    abstract fun bindArticleService(
        articleServiceImpl: ArticleServiceImpl,
    ): ArticleService
}