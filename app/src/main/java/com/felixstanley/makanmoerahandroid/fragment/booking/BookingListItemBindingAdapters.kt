package com.felixstanley.makanmoerahandroid.fragment.booking

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.felixstanley.makanmoerahandroid.R
import com.felixstanley.makanmoerahandroid.entity.booking.Booking
import com.felixstanley.makanmoerahandroid.entity.enums.BookingStatus
import com.google.android.material.chip.Chip

@BindingAdapter("bookingStatus")
fun bindBookingStatus(chip: Chip, booking: Booking) {
    booking?.let {
        val chipColor = when (it.bookingStatus) {
            BookingStatus.ONGOING.ordinal.toShort() -> R.color.warning
            BookingStatus.COMPLETED.ordinal.toShort() -> R.color.success
            BookingStatus.CANCELLED.ordinal.toShort() -> R.color.danger
            else -> R.color.warning
        }
        val chipText = when (it.bookingStatus) {
            BookingStatus.ONGOING.ordinal.toShort() -> BookingStatus.ONGOING.name
            BookingStatus.COMPLETED.ordinal.toShort() -> BookingStatus.COMPLETED.name
            // TODO: Find a way not to hardcode the String
            BookingStatus.CANCELLED.ordinal.toShort() -> if (it.cancelledBySystem) "Cancelled By System" else BookingStatus.CANCELLED.name
            else -> BookingStatus.ONGOING.name
        }
        // Set Chip Background Color to Obtained Color based on BookingStatus
        chip.setChipBackgroundColorResource(chipColor)
        chip.text = chipText
    }
}

@BindingAdapter("numOfPeople")
fun bindNumOfPeople(textView: TextView, numOfPeople: Short) {
    // Apply Correct Singular / Plural Form
    textView.text = if (numOfPeople == 1.toShort()) "$numOfPeople Person" else "$numOfPeople People"
}