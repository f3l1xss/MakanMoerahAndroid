package com.felixstanley.makanmoerahandroid.network.api

import com.felixstanley.makanmoerahandroid.network.Configuration.retrofit
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService

object RestaurantApi {
    val restaurantService: RestaurantService by lazy {
        retrofit.create(RestaurantService::class.java)
    }
}