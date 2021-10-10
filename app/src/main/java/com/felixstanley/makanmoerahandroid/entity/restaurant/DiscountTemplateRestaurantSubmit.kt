package com.felixstanley.makanmoerahandroid.entity.restaurant

data class DiscountTemplateRestaurantSubmit(
    val discountDetailsId: Int,
    val name: String,
    val minimumSpends: List<RestaurantMinimumSpend>
)