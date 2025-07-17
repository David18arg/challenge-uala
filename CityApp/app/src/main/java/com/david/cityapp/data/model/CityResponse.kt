package com.david.cityapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CityResponse(
    val name: String,
    val country: String,
    val coord: Coord
)

@Serializable
data class Coord(
    val lon: Float,
    val lat: Float
)