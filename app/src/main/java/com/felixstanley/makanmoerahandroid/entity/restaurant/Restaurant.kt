package com.felixstanley.makanmoerahandroid.entity.restaurant

import com.felixstanley.makanmoerahandroid.entity.coordinate.GeoPoint
import com.felixstanley.makanmoerahandroid.entity.facility.Facility
import com.felixstanley.makanmoerahandroid.entity.paymentmode.PaymentMode

data class Restaurant(
    val id: Int,
    val name: String,
    val address: String,
    val city: String,
    val district: String,
    val coordinate: GeoPoint,
    val restaurantHolidayHours: List<RestaurantHolidayHours>?,
    val restaurantImages: List<RestaurantImage>,
    val menus: List<Menu>?,
    val facilities: List<Facility>,
    val paymentModes: List<PaymentMode>?,
    val reviewElastics: List<ReviewElastic>?,
    val restaurantCapacities: List<RestaurantCapacity>?,
    val phone: String,
    val foodCategory: String,
    val priceCategory: Short,
    val averageStar: Double,
    val description: String
)