package com.sundayting.wancompose.page.homescreen.mine.repo

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.room.withTransaction
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.NeedLoginAgainEvent
import com.sundayting.wancompose.datastore.dataStore
import com.sundayting.wancompose.db.WanDatabase
import com.sundayting.wancompose.function.UserLoginFunction
import com.sundayting.wancompose.function.UserLoginFunction.CURRENT_LOGIN_ID_KEY
import com.sundayting.wancompose.function.UserLoginFunction.UserInfoBean
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.network.requireData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MineRepository @Inject constructor(
    private val userService: UserService,
    private val database: WanDatabase,
    @ApplicationContext context: Context,
) {

    init {
        MainScope().launch {
            launch {
                EventManager.eventFlow.filterIsInstance<NeedLoginAgainEvent>().collect {
                    clearLoginUser()
                }
            }
        }

    }

    private val dataStore = context.dataStore

    val curUserFlow = dataStore.data
        .mapLatest { it[CURRENT_LOGIN_ID_KEY] }
        .flatMapLatest {
            database.userDao().currentUserFlow(it ?: 0)
        }

    private suspend fun login(
        username: String,
        password: String,
    ) = userService.login(username, password)


    /**
     * 登出
     */
    suspend fun logout() = userService.logout()

    private suspend fun fetchUserInfo() = userService.fetchUserInfo()

    suspend fun clearLoginUser() {
        coroutineScope {
            joinAll(
                launch {
                    dataStore.edit { mp ->
                        mp[CURRENT_LOGIN_ID_KEY] = 0
                    }
                },
                launch {
                    database.userDao().clear()
                }
            )
        }
    }

    suspend fun loginAndAutoInsertData(
        username: String,
        password: String,
    ): UserInfoBean? {
        return coroutineScope {
            val loginResult = login(username, password)
            return@coroutineScope if (loginResult.isSuccess()) {
                val userInfoResult = fetchUserInfo()
                if (userInfoResult.isSuccess()) {
                    val userInfoBean = userInfoResult.body.requireData()
                    joinAll(
                        launch {
                            database.withTransaction {
                                database.userDao().clear()
                                database.userDao().insertUser(
                                    UserLoginFunction.UserEntity(
                                        id = userInfoBean.userInfo.id,
                                        nick = userInfoBean.userInfo.nickname,
                                        coinCount = userInfoBean.coinInfo.coinCount,
                                        level = userInfoBean.coinInfo.level,
                                        rank = userInfoBean.coinInfo.rank
                                    )
                                )
                            }
                        },
                        launch {
                            dataStore.edit { mp ->
                                mp[CURRENT_LOGIN_ID_KEY] = userInfoBean.userInfo.id
                            }
                        }
                    )
                    userInfoBean
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    suspend fun register(
        username: String,
        password: String,
        passwordAgain: String,
    ) = userService.register(username, password, passwordAgain)


}