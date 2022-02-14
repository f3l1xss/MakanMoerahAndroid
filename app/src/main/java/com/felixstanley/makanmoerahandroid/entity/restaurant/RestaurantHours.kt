package com.felixstanley.makanmoerahandroid.entity.restaurant

data class RestaurantHours(
    val openingHour: String,
    val closingHour: String,
    val dayOfWeek: Short,
    val dayOfWeekString: String
)