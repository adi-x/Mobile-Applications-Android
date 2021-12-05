package com.app.auth.data.remote

import com.app.auth.data.TokenHolder
import com.app.auth.data.User
import com.app.core.Api
import com.app.core.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

object RemoteAuthDataSource {
    interface AuthService {
        @Headers("Content-Type: application/json")
        @POST("login")
        suspend fun login(@Body user: User): String
//        suspend fun login(): TokenHolder
    }

    private val authService: AuthService = Api.retrofit.create(AuthService::class.java)

    suspend fun login(user: User): Result<TokenHolder> {
        try {
            val e = authService.login(user);
            val tokenHolder = TokenHolder(e);
//            val e = authService.login();
            println(e);
            return Result.Success(tokenHolder);
        } catch (e: Exception) {
            println(e);
            return Result.Error(e)
        }
    }
}

