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

    // Boolean Flag to Inform Fragment to stop Loading (New Data Set is Fetched)
    private val _newDataSetFetched = MutableLiveData<Boolean>()
    val newDataSetFetched: LiveData<Boolean>
        get() = _newDataSetFetched

    // Boolean Flag to Inform Fragment to Hide Bottom Sheet
    private val _hideBottomSheet = MutableLiveData<Boolean>()
    val hideBottomSheet: LiveData<Boolean>
        get() = _hideBottomSheet

    init {
        getRestaurant(1)
    }

    fun getNextDataSet() {
        // Obtain Next Page Of Restaurant (If Available)
        _restaurantEntitiesPage.value?.let {
            if (it.currentPage < it.totalPages) {
                getRestaurant(it.currentPage + 1)
            }
        }
    }

    fun resetNewDataSetFetchedFlag() {
        _newDataSetFetched.value = false
    }

    fun onCloseBottomSheet() {
        _hideBottomSheet.value = true
    }

    fun resetHideBottomSheetFlag() {
        _hideBottomSheet.value = false
    }

    private fun getRestaurant(pageNum: Int) {
        viewModelScope.launch {
            // TODO: Refrain from hardcoding below params
            val restaurantEntitiesPage = restaurantService.getRestaurant(
                pageNum,
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

            // Inform that NewDataSet is Fetched
            _newDataSetFetched.value = true
        }
    }

}