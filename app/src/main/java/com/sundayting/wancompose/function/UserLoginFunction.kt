package com.sundayting.wancompose.function

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.sundayting.wancompose.network.WanNetResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object UserLoginFunction {

    val CURRENT_LOGIN_ID_KEY = intPreferencesKey("当前登录的用户id")


    @Serializable
    data class UserBean(
        @SerialName("id")
        val id: Int,
        @SerialName("nickname")
        val nickname: String,
    )

    @Serializable
    data class CoinInfoBean(
        @SerialName("coinCount")
        val coinCount: Int,
        @SerialName("level")
        val level: Int,
        @SerialName("rank")
        val rank: Int,
    )

    @Serializable
    data class UserInfoBean(
        @SerialName("userInfo")
        val userInfo: UserBean,
        @SerialName("coinInfo")
        val coinInfo: CoinInfoBean,
    )

    @Serializable
    data class UserInfoNetBean(
        override val data: UserInfoBean?,
        override val errorCode: Int,
        override val errorMsg: String,
    ) : WanNetResult<UserInfoBean>()

    @Entity
    data class UserEntity(
        @PrimaryKey
        @ColumnInfo("id")
        val id: Int,

        @ColumnInfo("nick")
        val nick: String,
        @ColumnInfo("coinCount")
        val coinCount: Int,
        @ColumnInfo("level")
        val level: Int,
        @ColumnInfo("rank")
        val rank: Int,
    )

    @Dao
    interface UserDao {

        @Insert
        suspend fun insertUser(userEntity: UserEntity)

        @Query("SELECT * FROM UserEntity WHERE id IN (:userId)")
        fun currentUserFlow(userId: Int): Flow<UserEntity?>

        @Query("SELECT * FROM UserEntity WHERE id IN (:userId)")
        fun queryUserById(userId: Int): UserEntity?

        @Query("DELETE FROM UserEntity")
        suspend fun clear()

    }

}