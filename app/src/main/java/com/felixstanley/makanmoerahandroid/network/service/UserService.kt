package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.user.ChangePasswordUser
import com.felixstanley.makanmoerahandroid.entity.user.LoggedInUser
import com.felixstanley.makanmoerahandroid.entity.user.NewUser
import retrofit2.http.*
import java.io.File

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

    @GET(BASE_URL)
    suspend fun getLoggedInUser(): LoggedInUser

    @GET("$BASE_URL/isEmailExist")
    suspend fun isEmailExist(
        @Query("email") email: String
    ): Boolean

    @Multipart
    @POST("$BASE_URL/signup")
    suspend fun signup(
        @Part newUser: NewUser,
        @Query("idImageFile") idImageFile: File,
        @Query("isFromAndroid") isFromAndroid: Boolean = true
    )

    @Multipart
    @POST("$BASE_URL/uploadAvatar")
    suspend fun uploadAvatar(
        @Query("recaptchaToken") recaptchaToken: String,
        @Query("avatarImageFile") avatarImageFile: File,
        @Query("isFromAndroid") isFromAndroid: Boolean = true
    )

    @Multipart
    @POST("$BASE_URL/uploadId")
    suspend fun uploadId(
        @Query("recaptchaToken") recaptchaToken: String,
        @Query("idImageFile") idImageFile: File,
        @Query("isFromAndroid") isFromAndroid: Boolean = true
    )

    @GET("$BASE_URL/isUserEnabled")
    suspend fun isUserEnabled(
        @Query("email") email: String
    ): Boolean

    @POST("$BASE_URL/forgotPassword")
    suspend fun forgotPassword(
        @Query("forgotPasswordEmail") forgotPasswordEmail: String,
        @Query("recaptchaToken") recaptchaToken: String,
        @Query("isFromAndroid") isFromAndroid: Boolean = true
    )

    @GET("$BASE_URL/isForgotPasswordTokenExist")
    suspend fun isForgotPasswordTokenExist(
        @Query("email") email: String
    ): Boolean

    @POST("$BASE_URL/changePassword")
    suspend fun changePassword(
        @Body changePasswordUser: ChangePasswordUser,
        @Query("isFromAndroid") isFromAndroid: Boolean = true
    ): String

    @POST("$BASE_URL/convertUserToOwner")
    suspend fun convertUserToOwner(
        @Query("alternateEmail") alternateEmail: Boolean
    )

    @GET("$BASE_URL/isFBUserWithoutEmail")
    suspend fun isFBUserWithoutEmail(): Boolean
}