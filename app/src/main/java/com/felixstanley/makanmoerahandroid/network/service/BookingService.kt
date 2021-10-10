package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.EntitiesPage
import com.felixstanley.makanmoerahandroid.entity.booking.Booking
import com.felixstanley.makanmoerahandroid.entity.booking.BookingSubmit
import com.felixstanley.makanmoerahandroid.entity.booking.ConfirmAttendance
import com.felixstanley.makanmoerahandroid.entity.booking.SubmitReview
import com.felixstanley.makanmoerahandroid.entity.enums.BookingStatus
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDate

private const val BASE_URL = "booking"

interface BookingService {

    @GET(BASE_URL)
    suspend fun getBooking(
        @Query("pageNum") pageNum: Int,
        @Query("bookingCategory") bookingCategory: BookingStatus
    ): EntitiesPage<Booking>

    @GET("$BASE_URL/getRestaurantReservation")
    suspend fun getRestaurantReservation(
        @Query("pageNum") pageNum: Int,
        @Query("bookingCategory") bookingCategory: BookingStatus,
        @Query("date") date: LocalDate
    ): EntitiesPage<Booking>

    @POST("$BASE_URL/cancelBooking")
    suspend fun cancelBooking(
        @Body bookingId: Int
    ): String

    @POST("$BASE_URL/submitReview")
    suspend fun submitReview(
        @Body submitReview: SubmitReview
    )

    @POST("$BASE_URL/confirmBooking")
    suspend fun confirmBooking(
        @Body bookingSubmit: BookingSubmit
    ): String

    @POST("$BASE_URL/confirmAttendance")
    suspend fun confirmAttendance(
        @Body confirmAttendance: ConfirmAttendance
    ): String

}