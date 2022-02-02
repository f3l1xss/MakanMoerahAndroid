package com.felixstanley.makanmoerahandroid.fragment.reservation

import androidx.lifecycle.viewModelScope
import com.felixstanley.makanmoerahandroid.fragment.booking.AbstractBookingViewModel
import com.felixstanley.makanmoerahandroid.network.service.BookingService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class ReservationViewModel(private val bookingService: BookingService) :
    AbstractBookingViewModel() {

    private var currentDate = LocalDate.now() // Date in yyyy-MM-dd format

    // Stores Latest Value of Date selected via Date Edit Text
    // Will correspond to value of currentDate
    val dateEditTextCalendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")

    init {
        getBookingCurrentCriteria()
    }

    override fun getBookingCurrentCriteria() {
        viewModelScope.launch {
            val bookingEntitiesPage =
                bookingService.getRestaurantReservation(
                    currentPageNum,
                    currentBookingCategory,
                    currentDate
                )

            // Update our LiveData with fetched EntitiesPage
            _bookingEntitiesPage.value = bookingEntitiesPage
        }
    }

    fun getEditTextCalendarFormattedDate(): String {
        return dateFormat.format(dateEditTextCalendar.time)
    }

    fun setDateEditTextCalendar(year: Int, month: Int, day: Int) {
        dateEditTextCalendar.set(year, month, day)
    }

    fun updateCurrentDate(date: LocalDate) {
        currentDate = date
        resetCurrentPage()
        // Get Booking List based on updated Booking Category
        getBookingCurrentCriteria()
    }

}