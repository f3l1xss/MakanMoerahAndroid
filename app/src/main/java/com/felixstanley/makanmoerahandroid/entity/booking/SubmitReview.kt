package com.felixstanley.makanmoerahandroid.entity.booking

data class SubmitReview(
    val bookingId: Int,
    val review: String,
    val star: Short
)
