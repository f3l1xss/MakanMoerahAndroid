package com.felixstanley.makanmoerahandroid.network.service

import com.felixstanley.makanmoerahandroid.entity.paymentmode.PaymentMode
import retrofit2.http.GET

private const val BASE_URL = "paymentMode"

interface PaymentModeService {

    @GET(BASE_URL)
    suspend fun getPaymentModes(): List<PaymentMode>

}