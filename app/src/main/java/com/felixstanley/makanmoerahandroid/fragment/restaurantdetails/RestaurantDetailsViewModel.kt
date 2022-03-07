package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import com.felixstanley.makanmoerahandroid.network.Configuration.moshi
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
            val responseBody = restaurantService.getById(restaurantId, LocalDate.now(), 2)
            val responseContent = responseBody.string()

            // Only Update Restaurant LiveData Value if responseContent is not Blank
            // (Indicating that Restaurant is Approved (Viewable))
            if (responseContent.isNotBlank()) {
                // Update our LiveData with fetched restaurant
                val restaurant = moshi.adapter(Restaurant::class.java).fromJson(responseContent)!!
                _restaurant.value = restaurant
            }
        }
    }

}