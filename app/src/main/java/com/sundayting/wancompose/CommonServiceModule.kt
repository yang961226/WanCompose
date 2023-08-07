package com.sundayting.wancompose

import com.sundayting.wancompose.page.homescreen.article.repo.HomePageService
import com.sundayting.wancompose.page.homescreen.mine.repo.MineService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    fun provideHomePageService(
        retrofit: Retrofit,
    ): HomePageService {
        return retrofit.create()
    }

    @Provides
    fun provideMineService(
        retrofit: Retrofit,
    ): MineService {
        return retrofit.create()
    }

}