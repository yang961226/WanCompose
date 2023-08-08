package com.sundayting.wancompose.page.homescreen.mine.repo

import com.sundayting.wancompose.function.UserLoginFunction.UserBean
import com.sundayting.wancompose.function.UserLoginFunction.UserInfoBean
import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.network.okhttp.NResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MineService {


    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): NResult<WanNResult<UserBean>>

    @GET("user/logout/json")
    suspend fun logout(): NResult<WanNResult<Any>>

    @GET("user/lg/userinfo/json")
    suspend fun fetchUserInfo(): NResult<WanNResult<UserInfoBean>>


}