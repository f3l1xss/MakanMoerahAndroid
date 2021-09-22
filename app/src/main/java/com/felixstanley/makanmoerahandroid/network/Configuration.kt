package com.felixstanley.makanmoerahandroid.network

import com.felixstanley.makanmoerahandroid.network.cookie.SessionCookieJar
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Configuration {

    private const val BASE_URL = "https://makanmoerah.com/api/"
    private val okHttpClient = OkHttpClient.Builder().cookieJar(SessionCookieJar()).build()
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
}