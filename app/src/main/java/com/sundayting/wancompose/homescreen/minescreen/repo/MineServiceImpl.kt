package com.sundayting.wancompose.homescreen.minescreen.repo

import com.sundayting.wancompose.network.Ktor
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MineServiceImpl @Inject constructor() : MineService {


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

    override suspend fun login(username: String, password: String): MineService.LoginBean {
        return Ktor.client.post(
            User.Login(User())
        ) {
            contentType(ContentType.Application.Json)
            setBody(LoginBody(username, password))
        }.body()
    }

}