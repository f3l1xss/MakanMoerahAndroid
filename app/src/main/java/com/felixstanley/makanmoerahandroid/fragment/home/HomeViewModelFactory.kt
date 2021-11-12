package com.felixstanley.makanmoerahandroid.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService

class HomeViewModelFactory(private val restaurantService: RestaurantService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(restaurantService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}