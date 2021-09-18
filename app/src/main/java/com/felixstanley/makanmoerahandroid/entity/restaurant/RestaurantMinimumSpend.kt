package com.felixstanley.makanmoerahandroid.entity.restaurant

data class RestaurantMinimumSpend(
    val discountDetailsId: Int?,
    val minSpend: Double,
    val discountAmount: Int,
    val discountDetailsName: String?
)