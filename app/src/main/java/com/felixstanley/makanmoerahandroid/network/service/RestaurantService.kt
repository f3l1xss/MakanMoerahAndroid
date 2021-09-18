package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.EntitiesPage
import com.felixstanley.makanmoerahandroid.entity.enums.RestaurantFilter
import com.felixstanley.makanmoerahandroid.entity.enums.SortCriteria
import com.felixstanley.makanmoerahandroid.entity.restaurant.Restaurant
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate

private const val BASE_URL = "restaurant"

interface RestaurantService {

    @GET(BASE_URL)
    suspend fun getRestaurant(
        @Query("pageNum") pageNum: Int,
        @Query("sort") sort: SortCriteria,
        @Query("currentLatitude") currentLatitude: Double,
        @Query("currentLongitude") currentLongitude: Double,
        @Query("cityId") cityId: Int,
        @Query("price") price: Int,
        @Query("minDiscount") minDiscount: Int,
        @Query("maxDiscount") maxDiscount: Int,
        @Query("minRating") minRating: Int,
        @Query("maxRating") maxRating: Int,
        @Query("date") date: LocalDate,
        @Query("timeslot") timeslot: Short,
        @Query("includeRemainingTimeslot") includeRemainingTimeslot: Boolean,
        @Query("numOfPeople") numOfPeople: Short,
        // Default to List of Empty String as Retrofit will 'drop' NULL Query Param
        @Query("districts") districts: List<String> = listOf(""),
        @Query("foodCategories") foodCategories: List<String> = listOf("")
    ): EntitiesPage<Restaurant>

    @GET("$BASE_URL/search")
    suspend fun searchRestaurant(
        @Query("searchTerm") searchTerm: String
    ): List<Restaurant>


    @GET("$BASE_URL/getById")
    suspend fun getById(
        @Query("id") id: Int,
        @Query("date") date: LocalDate,
        @Query("numOfPeople") numOfPeople: Int
    ): Restaurant

    @GET("$BASE_URL/filter")
    suspend fun getRestaurantFilter(
        @Query("filter") filter: RestaurantFilter,
        @Query("cityId") cityId: Int,
        @Query("date") date: LocalDate,
        @Query("timeslot") timeslot: Short,
        @Query("includeRemainingTimeslot") includeRemainingTimeslot: Boolean,
        @Query("numOfPeople") numOfPeople: Short,
        // Default to List of Empty String as Retrofit will 'drop' NULL Query Param
        @Query("districts") districts: List<String> = listOf(""),
    ): List<String>
}