package com.felixstanley.makanmoerahandroid.entity.restaurant

data class RestaurantCapacity(
    val date: String,
    val time: String,
    val timeslot: Int,
    val maxDiscount: Int,
    val remainingCapacity: Int,
    val restaurantMinimumSpends: List<RestaurantMinimumSpend>,
)