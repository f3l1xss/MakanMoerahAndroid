package com.felixstanley.makanmoerahandroid.fragment.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.felixstanley.makanmoerahandroid.entity.EntitiesPage
import com.felixstanley.makanmoerahandroid.entity.booking.Booking
import com.felixstanley.makanmoerahandroid.entity.enums.BookingStatus

abstract class AbstractBookingViewModel : ViewModel() {

    protected val _bookingEntitiesPage = MutableLiveData<EntitiesPage<Booking>>()
    val bookingEntitiesPage: LiveData<EntitiesPage<Booking>>
        get() = _bookingEntitiesPage

    // Current Selection Of Filters (With Default Value)
    protected var currentBookingCategory = BookingStatus.ONGOING
    protected var currentPageNum = 1

    // Boolean Flag to keep state whether bookingListRefresh is due to infiniteScroll
    private var refreshTriggeredByInfiniteScroll = false

    fun getNextDataSet() {
        // Obtain Next Page Of Booking List (If Available)
        _bookingEntitiesPage.value?.let {
            if (it.currentPage < it.totalPages) {
                currentPageNum = it.currentPage + 1
                refreshTriggeredByInfiniteScroll = true
                getBookingCurrentCriteria()
            }
        }
    }

    fun isRefreshTriggeredByInfiniteScroll(): Boolean {
        return refreshTriggeredByInfiniteScroll
    }

    fun resetRefreshTriggeredByInfiniteScroll() {
        refreshTriggeredByInfiniteScroll = false
    }

    fun updateCurrentBookingCategory(bookingStatus: BookingStatus) {
        currentBookingCategory = bookingStatus
        resetCurrentPage()
        // Get Booking List based on updated Booking Category
        getBookingCurrentCriteria()
    }

    protected fun resetCurrentPage() {
        currentPageNum = 1
    }

    protected abstract fun getBookingCurrentCriteria()

}