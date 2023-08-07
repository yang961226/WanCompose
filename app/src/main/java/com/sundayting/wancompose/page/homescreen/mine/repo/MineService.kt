package com.sundayting.wancompose.page.homescreen.mine.repo

import com.sundayting.wancompose.function.UserLoginFunction.UserBean
import com.sundayting.wancompose.function.UserLoginFunction.UserInfoBean
import com.sundayting.wancompose.network.okhttp.NResult
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface MineService {


    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): NResult<UserBean>

    @GET("user/logout/json")
    suspend fun logout(): NResult<Any>

    @GET("user/lg/userinfo/json")
    suspend fun fetchUserInfo(): NResult<UserInfoBean>


}