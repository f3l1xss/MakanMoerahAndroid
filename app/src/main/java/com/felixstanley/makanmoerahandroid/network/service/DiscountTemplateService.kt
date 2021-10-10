package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.discounttemplate.DiscountTemplateResponse
import retrofit2.http.GET

private const val BASE_URL = "discountTemplate"

interface DiscountTemplateService {

    @GET(BASE_URL)
    suspend fun getDiscountTemplate(): List<DiscountTemplateResponse>

}