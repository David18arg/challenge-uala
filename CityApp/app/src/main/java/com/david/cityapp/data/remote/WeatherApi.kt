package com.david.cityapp.data.remote

import com.david.cityapp.data.model.WeatherResponse
import com.david.cityapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "sp"
    ): WeatherResponse
}