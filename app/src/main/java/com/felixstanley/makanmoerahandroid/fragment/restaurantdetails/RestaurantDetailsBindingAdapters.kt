package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant

@BindingAdapter("holidayHourNotice")
fun bindHolidayHourNotice(textView: TextView, restaurant: Restaurant?) {
    restaurant?.restaurantHolidayHours?.let {
        if (it.isNotEmpty()) {
            val holidayHour = it[0]
            textView.text =
                "Due to current Holiday, Operating Hour from " +
                        "${holidayHour.validFrom} to ${holidayHour.validTo} " +
                        "is from ${holidayHour.openingHour} to ${holidayHour.closingHour}. " +
                        "Please verify with Restaurant for timing confirmation before making a booking."
        }
    }
}

@BindingAdapter("strikeThroughText")
fun bindStrikeThroughText(textView: TextView, text: String) {
    textView.text = text
    // Trigger Strike Through Flag by performing OR with existing Paint Flags
    textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}