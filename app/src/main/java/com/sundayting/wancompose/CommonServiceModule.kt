package com.sundayting.wancompose

import com.sundayting.wancompose.page.homescreen.article.repo.ArticleService
import com.sundayting.wancompose.page.homescreen.mine.point.repo.PointService
import com.sundayting.wancompose.page.homescreen.mine.repo.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    fun provideArticleService(
        ktorfit: Ktorfit,
    ): ArticleService {
        return ktorfit.create()
    }

    @Provides
    fun provideUserService(
        ktorfit: Ktorfit,
    ): UserService {
        return ktorfit.create()
    }

    @Provides
    fun providePointService(
        ktorfit: Ktorfit,
    ): PointService {
        return ktorfit.create()
    }


}