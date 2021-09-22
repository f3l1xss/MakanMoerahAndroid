package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.facility.Facility
import retrofit2.http.GET

private const val BASE_URL = "facility"

interface FacilityService {

    @GET(BASE_URL)
    suspend fun getFacilities(): List<Facility>
}