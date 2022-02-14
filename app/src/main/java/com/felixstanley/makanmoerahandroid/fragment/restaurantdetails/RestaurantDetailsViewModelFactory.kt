package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService

class RestaurantDetailsViewModelFactory(
    private val restaurantService: RestaurantService,
    private val restaurantId: Int
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantDetailsViewModel::class.java)) {
            return RestaurantDetailsViewModel(restaurantService, restaurantId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}