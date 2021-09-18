package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.city.City
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "location"

interface LocationService {

    @GET("$BASE_URL/currentcity")
    suspend fun getCurrentCity(
        @Query("currentLatitude") currentLatitude: Double,
        @Query("currentLongitude") currentLongitude: Double,
    ): City
}