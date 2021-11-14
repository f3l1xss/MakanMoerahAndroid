package com.felixstanley.makanmoerahandroid.fragment.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService

class ExploreViewModelFactory(private val restaurantService: RestaurantService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            return ExploreViewModel(restaurantService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}