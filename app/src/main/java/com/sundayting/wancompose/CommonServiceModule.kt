package com.sundayting.wancompose

import com.sundayting.wancompose.homescreen.article.repo.HomePageService
import com.sundayting.wancompose.homescreen.article.repo.HomePageServiceImpl
import com.sundayting.wancompose.homescreen.minescreen.repo.MineService
import com.sundayting.wancompose.homescreen.minescreen.repo.MineServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonServiceModule {

    @Singleton
    @Binds
    abstract fun bindHomePageService(
        homePageServiceImpl: HomePageServiceImpl,
    ): HomePageService

    @Singleton
    @Binds
    abstract fun bindMineService(
        mineServiceImpl: MineServiceImpl,
    ): MineService

}