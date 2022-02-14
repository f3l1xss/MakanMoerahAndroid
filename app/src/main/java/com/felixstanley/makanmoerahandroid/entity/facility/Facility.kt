package com.felixstanley.makanmoerahandroid.entity.facility

data class Facility(
    override val name: String,
    override val iconClass: String
) : AbstractFacilityPaymentMode()