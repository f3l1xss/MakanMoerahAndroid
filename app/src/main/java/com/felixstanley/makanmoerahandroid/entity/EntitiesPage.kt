package com.felixstanley.makanmoerahandroid.entity

data class EntitiesPage<T : Any>(
    val entities: List<T>,
    val totalPages: Int,
    val numOfElements: Long,
    val currentPage: Int
)
