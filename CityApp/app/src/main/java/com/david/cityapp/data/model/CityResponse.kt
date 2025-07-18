package com.david.cityapp.data.model

import com.david.cityapp.domain.model.City
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

fun CityResponse.toDomain(): City = City(
    name = this.name,
    country = this.country,
    lat = this.coord.lat,
    lon = this.coord.lon,
    isFavorite = false
)