package com.sundayting.wancompose.page.homescreen.mine.repo

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.room.withTransaction
import com.sundayting.wancompose.datastore.dataStore
import com.sundayting.wancompose.db.WanDatabase
import com.sundayting.wancompose.function.UserLoginFunction.CURRENT_LOGIN_ID_KEY
import com.sundayting.wancompose.function.UserLoginFunction.UserEntity
import com.sundayting.wancompose.function.UserLoginFunction.UserInfoBean
import com.sundayting.wancompose.network.NetResult
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MineRepository @Inject constructor(
    private val mineService: MineService,
    private val database: WanDatabase,
    @ApplicationContext context: Context,
) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MineRepositoryEntryPoint {
        fun mineRepository(): MineRepository
    }

    companion object {

        fun getInstance(context: Application): MineRepository {
            return EntryPointAccessors.fromApplication(
                context,
                MineRepositoryEntryPoint::class.java
            ).mineRepository()
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
    ) = mineService.login(username, password)

    private suspend fun fetchUserInfo() = mineService.fetchUserInfo()

    suspend fun clearLoginUser() {
        dataStore.edit { mp ->
            mp[CURRENT_LOGIN_ID_KEY] = 0
        }
        database.userDao().clear()
    }

    suspend fun loginAndAutoInsertData(
        username: String,
        password: String,
    ): UserInfoBean? {
        return coroutineScope {
            val loginResult = login(username, password)
            return@coroutineScope if (loginResult is NetResult.Success) {
                val fetchUserInfoResult = fetchUserInfo()
                if (fetchUserInfoResult is NetResult.Success) {
                    fetchUserInfoResult.body.data.also {
                        if (it != null) {
                            joinAll(
                                launch {
                                    database.withTransaction {
                                        database.userDao().clear()
                                        database.userDao().insertUser(
                                            UserEntity(
                                                id = it.userInfo.id,
                                                nick = it.userInfo.nickname,
                                                coinCount = it.coinInfo.coinCount,
                                                level = it.coinInfo.level,
                                                rank = it.coinInfo.rank
                                            )
                                        )
                                    }
                                },
                                launch {
                                    dataStore.edit { mp ->
                                        mp[CURRENT_LOGIN_ID_KEY] = it.userInfo.id
                                    }
                                }
                            )
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


}