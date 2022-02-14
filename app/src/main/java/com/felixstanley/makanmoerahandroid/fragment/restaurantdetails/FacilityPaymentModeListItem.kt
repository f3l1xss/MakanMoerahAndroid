package com.felixstanley.makanmoerahandroid.fragment.restaurantdetails

import com.felixstanley.makanmoerahandroid.entity.facility.AbstractFacilityPaymentMode

// Representation of Facility Payment Mode List Item displayed at Restaurant Details Fragment
data class FacilityPaymentModeListItem(
    val firstItem: AbstractFacilityPaymentMode,
    val secondItem: AbstractFacilityPaymentMode?
)
