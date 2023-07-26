package com.sundayting.wancompose

import com.sundayting.wancompose.homescreen.article.repo.HomePageService
import com.sundayting.wancompose.homescreen.article.repo.HomePageServiceImpl
import com.sundayting.wancompose.homescreen.minescreen.repo.MineService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonServiceModule {

    @Singleton
    @Binds
    abstract fun bindHomePageService(
        homePageServiceImpl: HomePageServiceImpl,
    ): HomePageService

//    @Singleton
//    @Binds
//    abstract fun bindMineService(
//        mineServiceImpl: MineServiceImpl,
//    ): MineService


}

@Module
@InstallIn(SingletonComponent::class)
object CommonModule2 {

    @Provides
    @Singleton
    fun providerMineService(
        ktorfit: Ktorfit,
    ): MineService {
        return ktorfit.create()
    }

}