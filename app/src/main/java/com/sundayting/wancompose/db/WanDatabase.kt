package com.sundayting.wancompose.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sundayting.wancompose.function.ArticleFunction
import com.sundayting.wancompose.function.UserLoginFunction
import com.sundayting.wancompose.page.search.SearchDao
import com.sundayting.wancompose.page.search.SearchViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [
        UserLoginFunction.UserEntity::class,
        SearchViewModel.SearchItem::class
//        ArticleBean::class,
    ], version = 1
)
abstract class WanDatabase : RoomDatabase() {

    abstract fun userDao(): UserLoginFunction.UserDao

    abstract fun articleDao(): ArticleFunction.ArticleDao

    abstract fun historyDao(): SearchDao

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