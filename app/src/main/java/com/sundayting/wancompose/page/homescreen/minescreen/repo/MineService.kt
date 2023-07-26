package com.sundayting.wancompose.page.homescreen.minescreen.repo

import com.sundayting.wancompose.function.UserLoginFunction
import com.sundayting.wancompose.function.UserLoginFunction.UserInfoNetBean
import com.sundayting.wancompose.network.NetResult
import com.sundayting.wancompose.network.WanNetResult
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import kotlinx.serialization.Serializable


@Serializable
class LoginBean(
    override val data: UserLoginFunction.UserBean?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNetResult<UserLoginFunction.UserBean>()

interface MineService {


    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): NetResult<LoginBean>

    @GET("user/lg/userinfo/json")
    suspend fun fetchUserInfo(): NetResult<UserInfoNetBean>

}