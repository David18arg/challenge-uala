package com.david.cityapp.domain.model

data class City(
    val id: Long = 0,
    val name: String,
    val country: String,
    val lat: Float,
    val lon: Float,
    val isFavorite: Boolean = false
)