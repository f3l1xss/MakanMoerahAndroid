package com.felixstanley.makanmoerahandroid.network.service

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "user"

interface UserService {

    @FormUrlEncoded
    @POST("$BASE_URL/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("recaptchaToken") recaptchaToken: String,
        @Field("isFromAndroid") isFromAndroid: Boolean = true
    )

    @POST("$BASE_URL/forgotPassword")
    suspend fun forgotPassword(
        @Query("forgotPasswordEmail") forgotPasswordEmail: String,
        @Query("recaptchaToken") recaptchaToken: String,
        @Query("isFromAndroid") isFromAndroid: Boolean = true
    )
}