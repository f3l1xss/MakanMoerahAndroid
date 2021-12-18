package com.felixstanley.makanmoerahandroid.fragment.explore

import android.util.Range
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.entity.EntitiesPage
import com.felixstanley.makanmoerahandroid.entity.enums.RestaurantFilter
import com.felixstanley.makanmoerahandroid.entity.enums.SortCriteria
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import com.felixstanley.makanmoerahandroid.network.service.RestaurantService
import com.felixstanley.makanmoerahandroid.utility.Utility
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class ExploreViewModel(private val restaurantService: RestaurantService) : ViewModel() {

    private val _restaurantEntitiesPage = MutableLiveData<EntitiesPage<Restaurant>>()
    val restaurantEntitiesPage: LiveData<EntitiesPage<Restaurant>>
        get() = _restaurantEntitiesPage

    // Current Selection Of Filters (With Default Value)
    private var currentSort = SortCriteria.NEAREST
    private var currentNumOfPeople: Short = 2
    private var currentDate = LocalDate.now() // Date in yyyy-MM-dd format
    private var currentTimeslot = Utility.getTimeslot(LocalTime.now())
    private var currentCityId = Constants.NULL_REPRESENTATION
    private var currentDistricts = mutableListOf("")
    private var currentFoodCategories = mutableListOf("")
    private var currentPrice = Constants.NULL_REPRESENTATION
    private var currentDiscount =
        Range(Constants.NULL_REPRESENTATION, Constants.NULL_REPRESENTATION)
    private var currentRating = Range(Constants.NULL_REPRESENTATION, Constants.NULL_REPRESENTATION)
    private var currentIncludeRemainingTimeslot = true
    private var currentPageNum = 1

    // Stores Latest Value of Date selected via Date Edit Text
    // Will correspond to value of currentDate
    val dateEditTextCalendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")

    // Stores Latest Value of Time selected via Time Edit Text
    // Will correspond to value of currentTimeslot
    var timeEditTextTime = LocalTime.now()

    // Available (Rendered) City Id, City, District, Food Category Filters Value
    private val _cityIdFilters = MutableLiveData<List<Int>>()
    val cityIdFilters: LiveData<List<Int>>
        get() = _cityIdFilters

    private val _cityFilters = MutableLiveData<List<String>>()
    val cityFilters: LiveData<List<String>>
        get() = _cityFilters

    private val _districtFilters = MutableLiveData<List<String>>()
    val districtFilters: LiveData<List<String>>
        get() = _districtFilters

    private val _foodCategoryFilters = MutableLiveData<List<String>>()
    val foodCategoryFilters: LiveData<List<String>>
        get() = _foodCategoryFilters

    // Boolean Flag to Inform Fragment to Hide Bottom Sheet
    private val _hideBottomSheet = MutableLiveData<Boolean>()
    val hideBottomSheet: LiveData<Boolean>
        get() = _hideBottomSheet

    // Boolean Flag to keep state whether restaurantRefresh is due to infiniteScroll
    private var refreshTriggeredByInfiniteScroll = false

    // Boolean Flag to Inform Fragment that District Filters has just been reset
    private val _districtFiltersReset = MutableLiveData<Boolean>()
    val districtFiltersReset: LiveData<Boolean>
        get() = _districtFiltersReset

    // Boolean Flag to Inform Fragment that Food Category Filters has just been reset
    private val _foodCategoriesFiltersReset = MutableLiveData<Boolean>()
    val foodCategoriesFiltersReset: LiveData<Boolean>
        get() = _foodCategoriesFiltersReset

    init {
        getRestaurantCurrentCriteria()

        // Obtain Filters
        getFilters(RestaurantFilter.CITY_ID)
        getFilters(RestaurantFilter.CITY)
        getFilters(RestaurantFilter.DISTRICT)
        getFilters(RestaurantFilter.FOOD_CATEGORY)
    }

    fun getNextDataSet() {
        // Obtain Next Page Of Restaurant (If Available)
        _restaurantEntitiesPage.value?.let {
            if (it.currentPage < it.totalPages) {
                currentPageNum = it.currentPage + 1
                refreshTriggeredByInfiniteScroll = true
                getRestaurantCurrentCriteria()
            }
        }
    }

    fun setDateEditTextCalendar(year: Int, month: Int, day: Int) {
        dateEditTextCalendar.set(year, month, day)
    }

    fun getEditTextCalendarFormattedDate(): String {
        return dateFormat.format(dateEditTextCalendar.time)
    }

    fun setTimeEditTextTime(hour: Int, minute: Int) {
        timeEditTextTime = timeEditTextTime.withHour(hour).withMinute(minute)
    }

    fun getTimeEditTextTimeFormattedTime(): String {
        // Prepend with 0 if either hour or minute consist only of single digit
        val hour =
            if (timeEditTextTime.hour >= 10) "${timeEditTextTime.hour}" else "0${timeEditTextTime.hour}"
        val minute =
            if (timeEditTextTime.minute >= 10) "${timeEditTextTime.minute}" else "0${timeEditTextTime.minute}"
        // Update Time Edit Text Content to current value of timeEditTextTime
        return "$hour:$minute"
    }

    // Return Value of Current City Name Based on Current City Id
    fun getCurrentCityName(): String {
        // Perform a reverse mapping from cityId to cityName if currentCityId is not -1
        // (meaning that currentCityId has been populated before screen rotation)
        // Otherwise, return an empty string
        if (currentCityId == Constants.NULL_REPRESENTATION) {
            return ""
        }
        val cityNameIndex = _cityIdFilters.value?.indexOf(currentCityId)!!
        return _cityFilters.value?.get(cityNameIndex)!!
    }

    // Return Value of Current District Filters
    fun getCurrentDistricts(): List<String> {
        return currentDistricts.toList()
    }

    // Return Value of Current Food Categories Filters
    fun getCurrentFoodCategories(): List<String> {
        return currentFoodCategories.toList()
    }

    fun isRefreshTriggeredByInfiniteScroll(): Boolean {
        return refreshTriggeredByInfiniteScroll
    }

    fun resetRefreshTriggeredByInfiniteScroll() {
        refreshTriggeredByInfiniteScroll = false
    }

    fun isAtFirstPage(): Boolean {
        return currentPageNum == 1
    }

    fun onCloseBottomSheet() {
        _hideBottomSheet.value = true
    }

    fun resetHideBottomSheetFlag() {
        _hideBottomSheet.value = false
    }

    fun onApplyFilterButtonClicked() {
        // Reset Current Page Number and Refresh Restaurant
        resetCurrentPage()
        getRestaurantCurrentCriteria()
    }

    // Update Filter Selection Functions
    fun updateCurrentSort(sortCriteria: SortCriteria) {
        currentSort = sortCriteria
        resetCurrentPage()
    }

    fun updateCurrentNumOfPeople(numOfPeople: Short) {
        currentNumOfPeople = numOfPeople
        resetCurrentPage()
    }

    fun updateCurrentDate(date: LocalDate) {
        currentDate = date
        // Set Include Remaining Timeslot to false as Date is explicitly specified
        currentIncludeRemainingTimeslot = false

        // Reset Filters
        resetDistrictFilters()
        resetFoodCategoriesFilter()
        resetCurrentPage()
    }

    fun updateCurrentTimeslot(timeslot: Short) {
        currentTimeslot = timeslot
        // Set Include Remaining Timeslot to false as Timeslot is explicitly specified
        currentIncludeRemainingTimeslot = false

        // Reset Filters
        resetDistrictFilters()
        resetFoodCategoriesFilter()
        resetCurrentPage()
    }

    fun updateCurrentCityId(cityName: String, checked: Boolean) {
        // Only care about checked event as we can only select 1 city at a time
        // (Current Selection will cancel / remove the previous one)
        if (checked) {
            // Look for corresponding cityId from param cityName
            val cityIdIndex = _cityFilters.value?.indexOf(cityName)!!
            currentCityId = _cityIdFilters.value?.get(cityIdIndex)!!

            // Reset Filters
            resetDistrictFilters()
            resetFoodCategoriesFilter()
            resetCurrentPage()
        }
    }

    fun updateCurrentDistricts(value: String, checked: Boolean) {
        // If User is checking checkbox, add Value to Current Districts, Delete Otherwise
        if (checked) {
            currentDistricts.add(value)
        } else {
            val index = currentDistricts.indexOf(value)

            // Unchecked Event might be triggered by recyclerView during reset scenario,
            // This will cause item not to be found
            // Ignore this event
            if (index == -1) {
                return
            }
            currentDistricts.removeAt(index)
        }

        // Reset Filters
        resetFoodCategoriesFilter()
        resetCurrentPage()
    }

    fun updateCurrentFoodCategories(value: String, checked: Boolean) {
        // If User is checking checkbox, add Value to Current Food Categories, Delete Otherwise
        if (checked) {
            currentFoodCategories.add(value)
        } else {
            val index = currentFoodCategories.indexOf(value)

            // Unchecked Event might be triggered by recyclerView during reset scenario,
            // This will cause item not to be found
            // Ignore this event
            if (index == -1) {
                return
            }
            currentFoodCategories.removeAt(index)
        }

        resetCurrentPage()
    }

    fun updatePrice(price: Int) {
        currentPrice = price
        resetCurrentPage()
    }

    fun updateDiscount(minDiscount: Int, maxDiscount: Int) {
        currentDiscount = Range(minDiscount, maxDiscount)
        resetCurrentPage()
    }

    fun updateRating(minRating: Int, maxRating: Int) {
        currentRating = Range(minRating, maxRating)
        resetCurrentPage()
    }

    private fun resetCurrentPage() {
        // Reset Current Page to 1 due to filter change
        currentPageNum = 1
    }

    private fun resetDistrictFilters() {
        currentDistricts = mutableListOf("")
        getFilters(RestaurantFilter.DISTRICT)

        // Also Trigger Reset Flag
        _districtFiltersReset.value = true
    }

    fun resetDistrictFiltersResetFlag() {
        _districtFiltersReset.value = false
    }

    private fun resetFoodCategoriesFilter() {
        currentFoodCategories = mutableListOf("")
        getFilters(RestaurantFilter.FOOD_CATEGORY)

        // Also Trigger Reset Flag
        _foodCategoriesFiltersReset.value = true
    }

    fun resetFoodCategoriesResetFlag() {
        _foodCategoriesFiltersReset.value = false
    }

    private fun getRestaurantCurrentCriteria() {
        viewModelScope.launch {
            // TODO: Refrain from hardcoding below params
            val restaurantEntitiesPage = restaurantService.getRestaurant(
                currentPageNum,
                currentSort,
                1.3500416,
                103.8450688,
                currentCityId,
                currentPrice,
                currentDiscount.lower,
                currentDiscount.upper,
                currentRating.lower,
                currentRating.upper,
                currentDate,
                currentTimeslot,
                currentIncludeRemainingTimeslot,
                currentNumOfPeople,
                currentDistricts,
                currentFoodCategories
            )

            // Update our LiveData with fetched EntitiesPage
            _restaurantEntitiesPage.value = restaurantEntitiesPage
        }
    }

    private fun getFilters(restaurantFilter: RestaurantFilter) {
        viewModelScope.launch {
            val filters = restaurantService.getRestaurantFilter(
                restaurantFilter,
                currentCityId,
                currentDate,
                currentTimeslot,
                currentIncludeRemainingTimeslot,
                currentNumOfPeople,
                currentDistricts
            )

            // Correspondingly Assign to correct Filter Variables depending
            // upon restaurantFilter Parameter Received
            when (restaurantFilter) {
                RestaurantFilter.CITY_ID -> _cityIdFilters.value = filters.map { it -> it.toInt() }
                RestaurantFilter.CITY -> _cityFilters.value = filters
                RestaurantFilter.DISTRICT -> _districtFilters.value = filters
                RestaurantFilter.FOOD_CATEGORY -> _foodCategoryFilters.value = filters
            }
        }
    }

}