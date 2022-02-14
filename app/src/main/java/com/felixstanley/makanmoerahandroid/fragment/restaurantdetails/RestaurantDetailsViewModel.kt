package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService
import kotlinx.coroutines.launch
import java.time.LocalDate

class RestaurantDetailsViewModel(
    private val restaurantService: RestaurantService,
    private val restaurantId: Int
) : ViewModel() {

    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant>
        get() = _restaurant

    init {
        getRestaurantById()
    }

    private fun getRestaurantById() {
        viewModelScope.launch {
            // TODO: Refrain from hardcoding below params
            val restaurant = restaurantService.getById(restaurantId, LocalDate.now(), 2)

            // Update our LiveData with fetched restaurant
            _restaurant.value = restaurant
        }
    }

}