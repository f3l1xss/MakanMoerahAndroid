package com.felixstanley.makanmoerahandroid.network.api

import com.felixstanley.makanmoerahandroid.network.Configuration.retrofit
import com.felixstanley.makanmoerahandroid.network.service.*

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

    val facilityService: FacilityService by lazy {
        retrofit.create(FacilityService::class.java)
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}