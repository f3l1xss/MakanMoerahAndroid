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

    val bookingService: BookingService by lazy {
        retrofit.create(BookingService::class.java)
    }

    val createRestaurantService: CreateRestaurantService by lazy {
        retrofit.create(CreateRestaurantService::class.java)
    }

    val paymentModeService: PaymentModeService by lazy {
        retrofit.create(PaymentModeService::class.java)
    }

    val discountTemplateService: DiscountTemplateService by lazy {
        retrofit.create(DiscountTemplateService::class.java)
    }
}