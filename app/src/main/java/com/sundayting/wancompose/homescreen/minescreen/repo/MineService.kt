package com.sundayting.wancompose.homescreen.minescreen.repo

import com.sundayting.wancompose.network.WanNetResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

interface MineService {

    @Serializable
    class LoginBean(
        override val data: JsonElement?,
        override val errorCode: Int,
        override val errorMsg: String,
    ) : WanNetResult<JsonElement>()

    suspend fun login(
        username: String,
        password: String,
    ): LoginBean

}