package com.felixstanley.makanmoerahandroid.view.restaurantcardcarousel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.entity.enums.RestaurantCriteria
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService
import kotlinx.coroutines.launch
import java.time.LocalDate

class RestaurantCardCarouselViewModel(
    val restaurantService: RestaurantService,
    val criteria: RestaurantCriteria,
    val criteriaValue: String
) : ViewModel() {

    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>>
        get() = _restaurants

    init {
        viewModelScope.launch {
            // TODO: Refrain from hardcoding below params
            val restaurantList = restaurantService.getByCriteria(
                1.3226821,
                103.9054207,
                50,
                criteria,
                criteriaValue,
                LocalDate.now(),
                30,
                2
            )

            // Update our LiveData with fetched restaurantList
            _restaurants.value = restaurantList
        }
    }
}