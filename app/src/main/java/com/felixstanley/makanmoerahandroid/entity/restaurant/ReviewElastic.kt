package com.felixstanley.makanmoerahandroid.entity.restaurant

data class ReviewElastic(
    val bookingId: Int,
    val review: String,
    val star: Int,
    val usersName: String,
    val avatarFileName: String?,
    val visitDate: String
)