package com.felixstanley.makanmoerahandroid.fragment.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.entity.EntitiesPage
import com.felixstanley.makanmoerahandroid.entity.enums.SortCriteria
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExploreViewModel(private val restaurantService: RestaurantService) : ViewModel() {

    private val _restaurantEntitiesPage = MutableLiveData<EntitiesPage<Restaurant>>()
    val restaurantEntitiesPage: LiveData<EntitiesPage<Restaurant>>
        get() = _restaurantEntitiesPage

    init {
        viewModelScope.launch {
            // TODO: Refrain from hardcoding below params
            val restaurantEntitiesPage = restaurantService.getRestaurant(
                1,
                SortCriteria.NEAREST,
                1.3500416,
                103.8450688,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                LocalDate.now(),
                34,
                true,
                2
            )

            // Update our LiveData with fetched EntitiesPage
            _restaurantEntitiesPage.value = restaurantEntitiesPage
        }
    }

}