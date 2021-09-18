package com.felixstanley.makanmoerahandroid.network.api

import com.felixstanley.makanmoerahandroid.network.Configuration.retrofit
import com.felixstanley.makanmoerahandroid.network.service.JumbotronImageService
import com.felixstanley.makanmoerahandroid.network.service.LocationService
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService

object NetworkApi {

    val restaurantService: RestaurantService by lazy {
        retrofit.create(RestaurantService::class.java)
    }

    val jumbotronImageService: JumbotronImageService by lazy {
        retrofit.create(JumbotronImageService::class.java)
    }

    val locationService: LocationService by lazy {
        retrofit.create(LocationService::class.java)
    }
}