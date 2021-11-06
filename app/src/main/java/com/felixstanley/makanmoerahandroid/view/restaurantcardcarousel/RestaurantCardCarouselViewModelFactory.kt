package com.felixstanley.makanmoerahandroid.view.restaurantcardcarousel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.entity.enums.RestaurantCriteria
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService

class RestaurantCardCarouselViewModelFactory(
    private val restaurantService: RestaurantService, private val criteria: RestaurantCriteria,
    private val criteriaValue: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantCardCarouselViewModel::class.java)) {
            return RestaurantCardCarouselViewModel(restaurantService, criteria, criteriaValue) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}