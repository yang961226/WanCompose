package com.sundayting.wancompose.network.okhttp

import com.sundayting.wancompose.page.homescreen.article.repo.HomePageService2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object CommonServiceModule {

    @Provides
    fun provideHomePageService(
        retrofit: Retrofit,
    ): HomePageService2 {
        return retrofit.create(HomePageService2::class.java)
    }

}