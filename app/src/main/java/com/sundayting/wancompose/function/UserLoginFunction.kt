package com.sundayting.wancompose.function

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.sundayting.wancompose.network.WanNResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

object UserLoginFunction {

    /**
     * 游客的ID
     */
    const val VISITOR_ID = Long.MAX_VALUE

    val CURRENT_LOGIN_ID_KEY = intPreferencesKey("当前登录的用户id")


    @Serializable
    data class UserBean(
        val id: Int,
        val nickname: String,
    )

    @Serializable
    data class CoinInfoBean(
        val coinCount: Int,
        val level: Int,
        val rank: Int,
    )

    @Serializable
    data class UserInfoBean(
        val userInfo: UserBean,
        val coinInfo: CoinInfoBean,
    )

    @Entity
    data class UserEntity(
        @PrimaryKey
        val id: Long,
        val nick: String,
        val coinCount: Int,
        val level: Int,
        val rank: Int,
    )

    @Dao
    interface UserDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertUser(userEntity: UserEntity)

        @Query("SELECT * FROM UserEntity WHERE id IN (:userId)")
        fun currentUserFlow(userId: Int): Flow<UserEntity?>

        @Query("SELECT * FROM UserEntity WHERE id IN (:userId)")
        suspend fun queryUserById(userId: Int): UserEntity?

        @Query("DELETE FROM UserEntity")
        suspend fun clear()

    }

}

@Serializable
class UserInfoResultBean(
    override val data: UserLoginFunction.UserInfoBean?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<UserLoginFunction.UserInfoBean>()