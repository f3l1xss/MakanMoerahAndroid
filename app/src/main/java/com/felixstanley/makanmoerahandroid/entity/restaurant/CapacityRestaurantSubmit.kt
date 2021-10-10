package com.felixstanley.makanmoerahandroid.entity.restaurant

data class CapacityRestaurantSubmit(
    val dayOfWeek: Short,
    val restaurantCapacities: List<CapacityRestaurant>
)
