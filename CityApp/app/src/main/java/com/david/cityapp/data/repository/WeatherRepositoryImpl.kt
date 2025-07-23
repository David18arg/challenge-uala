package com.david.cityapp.data.repository

import android.util.Log
import com.david.cityapp.data.remote.WeatherApi
import com.david.cityapp.domain.model.Weather
import com.david.cityapp.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRepository {
    override suspend fun getWeatherByLocation(
        lat: Float,
        lon: Float
    ): Weather {
        val response = weatherApi.getWeather(lat, lon).toDomain()
        return response
    }
}