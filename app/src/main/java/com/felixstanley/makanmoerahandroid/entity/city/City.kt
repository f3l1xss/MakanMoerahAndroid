package com.felixstanley.makanmoerahandroid.entity.city

import com.felixstanley.makanmoerahandroid.entity.coordinate.GeoBox

data class City(
    val id: Int,
    val name: String,
    val coordinate: GeoBox
)
