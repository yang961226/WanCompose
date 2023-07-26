package com.sundayting.wancompose.homescreen.minescreen.repo

import com.sundayting.wancompose.network.NetResult
import com.sundayting.wancompose.network.WanNetResult
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.POST
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class LoginBean(
    override val data: JsonElement?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNetResult<JsonElement>()

interface MineService {


    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): NetResult<LoginBean>

}