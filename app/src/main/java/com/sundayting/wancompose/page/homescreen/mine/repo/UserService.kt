package com.sundayting.wancompose.page.homescreen.mine.repo

import com.sundayting.wancompose.function.UserInfoResultBean
import com.sundayting.wancompose.network.NResult
import com.sundayting.wancompose.network.WanEmptyNResult
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST


interface UserService {

    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): NResult<WanEmptyNResult>

    @POST("user/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") passwordAgain: String,
    ): NResult<WanEmptyNResult>

    @GET("user/logout/json")
    suspend fun logout(): NResult<WanEmptyNResult>

    @GET("user/lg/userinfo/json")
    suspend fun fetchUserInfo(): NResult<UserInfoResultBean>


}