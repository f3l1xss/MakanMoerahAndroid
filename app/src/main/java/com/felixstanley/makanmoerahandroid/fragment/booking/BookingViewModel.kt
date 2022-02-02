package com.felixstanley.makanmoerahandroid.fragment.booking

import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.network.service.BookingService
import kotlinx.coroutines.launch

class BookingViewModel(private val bookingService: BookingService) : AbstractBookingViewModel() {

    init {
        getBookingCurrentCriteria()
    }

    override fun getBookingCurrentCriteria() {
        viewModelScope.launch {
            val bookingEntitiesPage =
                bookingService.getBooking(currentPageNum, currentBookingCategory)

            // Update our LiveData with fetched EntitiesPage
            _bookingEntitiesPage.value = bookingEntitiesPage
        }
    }

}