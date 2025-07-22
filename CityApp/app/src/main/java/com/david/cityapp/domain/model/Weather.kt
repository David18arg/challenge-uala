package com.david.cityapp.domain.model

import com.david.cityapp.data.model.Clouds
import com.david.cityapp.data.model.Coordinates
import com.david.cityapp.data.model.MainWeatherData
import com.david.cityapp.data.model.SystemData
import com.david.cityapp.data.model.WeatherDescription
import com.david.cityapp.data.model.Wind

data class Weather(
    val coord: Coordinates,
    val weather: List<WeatherDescription>,
    val base: String,
    val main: MainWeatherData,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: SystemData,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)

data class Coordinates(
    val lon: Double,
    val lat: Double
)

data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class MainWeatherData(
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val seaLevel: Int? = null,
    val groundLevel: Int? = null,
    val tempKf: Double? = null
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

data class Clouds(
    val all: Int
)

data class SystemData(
    val type: Int? = null,
    val id: Int? = null,
    val message: Double? = null,
    val country: String = "",
    val sunrise: Long = 0,
    val sunset: Long = 0
)