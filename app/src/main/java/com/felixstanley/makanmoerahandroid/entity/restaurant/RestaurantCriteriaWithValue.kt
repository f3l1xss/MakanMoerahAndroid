package com.felixstanley.makanmoerahandroid.entity.restaurant

import com.felixstanley.makanmoerahandroid.entity.enums.RestaurantCriteria

data class RestaurantCriteriaWithValue(
    val criteria: RestaurantCriteria,
    val value: String
)
