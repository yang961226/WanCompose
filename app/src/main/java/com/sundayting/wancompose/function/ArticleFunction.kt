package com.sundayting.wancompose.function

import androidx.room.Dao

object ArticleFunction {

    @Dao
    interface ArticleDao {

//        @Insert(onConflict = OnConflictStrategy.REPLACE)
//        suspend fun insertArticle(articleBean: ArticleBean)
//
//        @Insert(onConflict = OnConflictStrategy.REPLACE)
//        suspend fun insertArticles(articleList: List<ArticleBean>)
//
//        @Query("SELECT * FROM ArticleBean WHERE ownerId IN (:uid) ORDER BY isStick DESC")
//        fun queryUsersArticleFlow(uid: Long): Flow<List<ArticleBean>>
//
//        @Query("DELETE FROM ArticleBean  WHERE ownerId IN (:uid)")
//        suspend fun deleteUsersArticle(uid: Long)

    }

}