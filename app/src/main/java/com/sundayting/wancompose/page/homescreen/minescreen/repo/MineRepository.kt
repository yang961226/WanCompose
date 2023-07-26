package com.sundayting.wancompose.page.homescreen.minescreen.repo

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.sundayting.wancompose.datastore.dataStore
import com.sundayting.wancompose.db.WanDatabase
import com.sundayting.wancompose.function.UserLoginFunction
import com.sundayting.wancompose.function.UserLoginFunction.CURRENT_LOGIN_ID_KEY
import com.sundayting.wancompose.function.UserLoginFunction.UserInfoBean
import com.sundayting.wancompose.network.NetResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MineRepository @Inject constructor(
    private val mineService: MineService,
    database: WanDatabase,
    @ApplicationContext context: Context,
) {

    private val userDao = database.userDao()
    private val dataStore = context.dataStore

    private suspend fun login(
        username: String,
        password: String,
    ) = mineService.login(username, password)

    private suspend fun fetchUserInfo() = mineService.fetchUserInfo()

    suspend fun loginAndAutoInsertData(
        username: String,
        password: String,
    ): UserInfoBean? {
        val loginResult = login(username, password)
        return if (loginResult is NetResult.Success) {
            val fetchUserInfoResult = fetchUserInfo()
            if (fetchUserInfoResult is NetResult.Success) {
                fetchUserInfoResult.data.data.also {
                    if (it != null) {
                        userDao.insertUser(
                            UserLoginFunction.UserEntity(
                                id = it.userInfo.id,
                                nick = it.userInfo.nickname,
                                coinCount = it.coinInfo.coinCount,
                                level = it.coinInfo.level,
                                rank = it.coinInfo.rank
                            )
                        )
                        dataStore.edit { mp ->
                            mp[CURRENT_LOGIN_ID_KEY]
                        }
                    }
                }
            } else {
                null
            }
        } else {
            null
        }
    }


}