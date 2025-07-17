package com.david.cityapp.domain.repository

import com.david.cityapp.domain.model.City

interface CityRepository {

    fun getCities(
        query: String,
        onlyFav: Boolean = false,
        pageSize: Int = 50
    ): List<City>


    suspend fun preloadCities()

}