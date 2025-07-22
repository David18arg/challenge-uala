package com.david.cityapp.data.model

import com.david.cityapp.domain.model.Weather
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
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
) {
    fun toDomain(): Weather = Weather(
        coord = this.coord,
        weather = this.weather,
        base = this.base,
        main = this.main,
        visibility = this.visibility,
        wind = this.wind,
        clouds = this.clouds,
        dt = this.dt,
        sys = this.sys,
        timezone = this.timezone,
        id = this.id,
        name = this.name,
        cod = this.cod
    )
}

@Serializable
data class Coordinates(
    val lon: Double,
    val lat: Double
)

@Serializable
data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class MainWeatherData(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    @SerialName("sea_level") val seaLevel: Int? = null,
    @SerialName("grnd_level") val groundLevel: Int? = null,
    @SerialName("temp_kf") val tempKf: Double? = null
)

@Serializable
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

@Serializable
data class Clouds(
    val all: Int
)

@Serializable
data class SystemData(
    val type: Int? = null,
    val id: Int? = null,
    val message: Double? = null,
    val country: String = "",
    val sunrise: Long = 0,
    val sunset: Long = 0
)

