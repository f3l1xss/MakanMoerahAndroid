package com.felixstanley.makanmoerahandroid.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.entity.restaurant.RestaurantCriteriaWithValue
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(val restaurantService: RestaurantService) : ViewModel() {

    private val _restaurantCriteriaWithValues = MutableLiveData<Set<RestaurantCriteriaWithValue>>()
    val restaurantCriteriaWithValues: LiveData<Set<RestaurantCriteriaWithValue>>
        get() = _restaurantCriteriaWithValues

    init {
        viewModelScope.launch {
            // TODO: Refrain from hardcoding below params
            val restaurantCriteriaWithValueSet =
                restaurantService.getCriteria(
                    1.3226821,
                    103.9054207, LocalDate.now(), 30, 2
                )

            // Update our LiveData with fetched Criterias
            _restaurantCriteriaWithValues.value = restaurantCriteriaWithValueSet
        }
    }
}