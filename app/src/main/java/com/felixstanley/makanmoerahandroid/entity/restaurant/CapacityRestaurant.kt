package com.felixstanley.makanmoerahandroid.entity.restaurant

import java.time.LocalTime

data class CapacityRestaurant(
    val startTimeString: LocalTime,
    val endTimeString: LocalTime,
    val capacity: Short,
    val discountDetailsId: Int
)
