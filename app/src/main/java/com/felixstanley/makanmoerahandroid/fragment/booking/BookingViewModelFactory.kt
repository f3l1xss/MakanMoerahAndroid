package com.felixstanley.makanmoerahandroid.fragment.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.network.service.BookingService

class BookingViewModelFactory(private val bookingService: BookingService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            return BookingViewModel(bookingService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}