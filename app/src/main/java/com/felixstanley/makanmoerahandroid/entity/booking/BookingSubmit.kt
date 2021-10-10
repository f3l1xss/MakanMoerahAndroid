package com.felixstanley.makanmoerahandroid.entity.booking

import java.time.LocalDate

data class BookingSubmit(
    val restaurantId: Int,
    val timeslot: Short,
    val dateString: LocalDate,
    val numOfPeople: Short
)
