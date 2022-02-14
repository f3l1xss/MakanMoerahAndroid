package com.felixstanley.makanmoerahandroid.entity.paymentmode

import com.felixstanley.makanmoerahandroid.entity.facility.AbstractFacilityPaymentMode

data class PaymentMode(
    override val name: String,
    override val iconClass: String
) : AbstractFacilityPaymentMode()