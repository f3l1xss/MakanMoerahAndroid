package com.felixstanley.makanmoerahandroid.entity.restaurant

import java.time.LocalTime

data class HoursRestaurant(
    val openingHourString: LocalTime,
    val closingHourString: LocalTime,
    val dayOfWeek: Short
)
