package com.felixstanley.makanmoerahandroid.entity.restaurant

import java.time.LocalDate
import java.time.LocalTime

data class HolidayHoursRestaurant(
    val validFromString: LocalDate,
    val validToString: LocalDate,
    val openingHourString: LocalTime,
    val closingHourString: LocalTime
)
