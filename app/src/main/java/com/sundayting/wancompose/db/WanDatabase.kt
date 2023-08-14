package com.sundayting.wancompose.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sundayting.wancompose.function.UserLoginFunction
import com.sundayting.wancompose.page.homescreen.article.ArticleBean
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [
        UserLoginFunction.UserEntity::class,
        ArticleBean::class
    ], version = 1
)
abstract class WanDatabase : RoomDatabase() {

    abstract fun userDao(): UserLoginFunction.UserDao

}

@Module
@InstallIn(SingletonComponent::class)
object WanDatabaseModule {


    @Provides
    @Singleton
    fun provideWanDatabase(
        @ApplicationContext context: Context,
    ): WanDatabase {
        return Room.databaseBuilder(context, WanDatabase::class.java, "database-wan").build()
    }

}