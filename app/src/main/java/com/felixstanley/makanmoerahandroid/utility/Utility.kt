package com.felixstanley.makanmoerahandroid.utility

import android.content.SharedPreferences
import com.felixstanley.makanmoerahandroid.constants.Constants
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.math.floor

object Utility {

    fun getTimeslot(localTime: LocalTime): Short {
        val hourMinuteDouble = ((localTime.getHour() * 60) + localTime.getMinute()).toDouble()
        return (floor(
            hourMinuteDouble / Constants.DEFAULT_BOOKING_MINUTE_INTERVAL
        ).toInt() + 1).toShort();
    }

    fun toLocalDate(calendar: Calendar): LocalDate {
        val year = calendar.get(Calendar.YEAR)
        // Handle 0 based month by adding 1
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return LocalDate.of(year, month, day)
    }

    fun credentialsExistAtSharedPreferences(sharedPreferences: SharedPreferences): Boolean {
        val emailKey = Constants.SHARED_PREFERENCES_LOGIN_EMAIL_KEY
        val passwordKey = Constants.SHARED_PREFERENCES_LOGIN_PASSWORD_KEY

        // Validate that Login Email & Login Password exists at shared preferences
        val email = sharedPreferences.getString(emailKey, null)
        val password = sharedPreferences.getString(passwordKey, null)
        return email != null && password != null
    }
}