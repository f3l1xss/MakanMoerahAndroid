package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

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