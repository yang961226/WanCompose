package com.sundayting.wancompose.homescreen.minescreen.repo

import com.sundayting.wancompose.network.Ktor
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.resources.Resource

class MineServiceImpl : MineService {


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

    private class LoginBody(
        val username: String,
        val password: String,
    )

    override suspend fun login(username: String, password: String): MineService.LoginBean {
        return Ktor.client.post(
            User.Login(User())
        ) {
            setBody(LoginBody(username, password))
        }.body()
    }

}