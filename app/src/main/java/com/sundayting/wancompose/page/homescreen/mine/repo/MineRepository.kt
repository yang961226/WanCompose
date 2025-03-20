package com.sundayting.wancompose.page.homescreen.mine.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.NeedLoginAgainEvent
import com.sundayting.wancompose.db.WanDatabase
import com.sundayting.wancompose.function.UserLoginFunction
import com.sundayting.wancompose.function.UserLoginFunction.CURRENT_LOGIN_ID_KEY
import com.sundayting.wancompose.function.UserLoginFunction.UserInfoBean
import com.sundayting.wancompose.function.UserLoginFunction.VISITOR_ID
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.network.requireData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MineRepository @Inject constructor(
    private val userService: UserService,
    private val database: WanDatabase,
    private val eventManager: EventManager,
    private val dataStore: DataStore<Preferences>,
) {

    private val scope = CoroutineScope(SupervisorJob())

    init {
        scope.launch {
            launch {
                eventManager.eventFlow.filterIsInstance<NeedLoginAgainEvent>().collect {
                    clearLoginUser()
                }
            }
        }
    }

    val curUserFlow = dataStore.data
        .mapLatest { it[CURRENT_LOGIN_ID_KEY] }
        .flatMapLatest {
            database.userDao().currentUserFlow(it ?: 0)
        }.stateIn(scope, SharingStarted.Eagerly, null)

    val curUidFlow = curUserFlow.mapLatest { it?.id ?: VISITOR_ID }.stateIn(
        scope, SharingStarted.Eagerly,
        VISITOR_ID
    )

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
                            database.userDao().insertUser(
                                UserLoginFunction.UserEntity(
                                    id = userInfoBean.userInfo.id.toLong(),
                                    nick = userInfoBean.userInfo.nickname,
                                    coinCount = userInfoBean.coinInfo.coinCount,
                                    level = userInfoBean.coinInfo.level,
                                    rank = userInfoBean.coinInfo.rank.toIntOrNull() ?: -1
                                )
                            )
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