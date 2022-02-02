package com.felixstanley.makanmoerahandroid.fragment.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felixstanley.makanmoerahandroid.network.service.BookingService

class ReservationViewModelFactory(private val bookingService: BookingService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            return ReservationViewModel(bookingService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}