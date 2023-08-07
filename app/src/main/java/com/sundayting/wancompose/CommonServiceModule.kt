package com.sundayting.wancompose

import com.sundayting.wancompose.page.homescreen.mine.repo.MineService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    @Singleton
    fun providerMineService(
        ktorfit: Ktorfit,
    ): MineService {
        return ktorfit.create()
    }

}