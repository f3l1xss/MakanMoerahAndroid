package com.felixstanley.makanmoerahandroid.entity.booking

import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import com.felixstanley.makanmoerahandroid.entity.restaurant.RestaurantMinimumSpend
import com.felixstanley.makanmoerahandroid.entity.restaurant.ReviewElastic

data class Booking(
    val id: Int,
    val usersId: String,
    val usersName: String,
    val usersEmail: String,
    val restaurant: Restaurant,
    val confirmedDate: String,
    val timeslotLocalTime: String,
    val bookingTime: String,
    val bookingCode: String,
    val numOfPeople: Short,
    val bookingStatus: Short,
    val cancelledBySystem: Boolean,
    val confirmAttendanceCode: String,
    val discountDetailsId: Int,
    val minimumSpends: List<RestaurantMinimumSpend>,
    val maxDiscount: Int,
    val review: ReviewElastic,
)
