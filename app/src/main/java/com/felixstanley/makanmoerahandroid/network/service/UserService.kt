package com.felixstanley.makanmoerahandroid.network.service

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private const val BASE_URL = "user"

interface UserService {

    @FormUrlEncoded
    @POST("$BASE_URL/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("recaptchaToken") recaptchaToken: String
    )
}