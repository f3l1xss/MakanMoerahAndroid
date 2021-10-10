package com.felixstanley.makanmoerahandroid.entity.restaurant

import com.felixstanley.makanmoerahandroid.entity.paymentmode.PaymentMode

data class PaymentModeRestaurantSubmit(
    val restaurantPaymentModes: List<PaymentMode>
)
