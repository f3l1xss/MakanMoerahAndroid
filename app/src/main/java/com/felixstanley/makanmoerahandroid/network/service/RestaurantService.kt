package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.Restaurant
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "restaurant/"

interface RestaurantService {

    @GET(BASE_URL + "getById")
    suspend fun getById(
        @Query("id") id: Int,
        @Query("date") date: String,
        @Query("numOfPeople") numOfPeople: Int
    ): Restaurant
}