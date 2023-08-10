package com.sundayting.wancompose

import com.sundayting.wancompose.page.homescreen.article.repo.HomePageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    fun provideHomePageService(
        ktorfit: Ktorfit,
    ): HomePageService {
        return ktorfit.create()
    }


}