package com.sundayting.wancompose.page.homescreen.minescreen.repo

import io.ktor.client.HttpClient
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MineServiceImpl @Inject constructor(
    private val client: HttpClient,
) {


    @Resource("/user")
    private class User {
        @Resource("login")
        class Login(val parent: User = User())

        @Resource("register")
        class Register(val parent: User = User())

        @Resource("logout")
        class Logout(val parent: User = User()) {

            @Resource("json")
            class Json(val parent: Logout = Logout())

        }
    }

    @Serializable
    private class LoginBody(
        val username: String,
        val password: String,
    )

//    override suspend fun login(username: String, password: String): NetResult<Any> {
//        TODO("Not yet implemented")
//    }

//    override suspend fun login(username: String, password: String): MineService.LoginBean {
//        return client.post(
//            User.Login(User())
//        ) {
//            contentType(ContentType.Application.FormUrlEncoded)
//            setBody(FormDataContent(
//                Parameters.build {
//                    append("username", username)
//                    append("password", password)
//                }
//            ))
//        }.body()
//    }

}