package com.sundayting.wancompose.homescreen.minescreen.repo

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MineRepository @Inject constructor(
    private val mineService: MineService,
) {

    suspend fun login(
        username: String,
        password: String,
    ) = mineService.login(username, password)

}