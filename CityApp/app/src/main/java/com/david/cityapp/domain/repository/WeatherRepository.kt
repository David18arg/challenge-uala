package com.david.cityapp.domain.repository

import com.david.cityapp.domain.model.Weather

interface WeatherRepository {

    suspend fun getWeatherByLocation(lat: Float, lon: Float): Weather
}