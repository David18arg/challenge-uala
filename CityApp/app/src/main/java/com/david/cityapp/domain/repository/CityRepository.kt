package com.david.cityapp.domain.repository

import androidx.paging.PagingData
import com.david.cityapp.domain.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {

    fun getCities(
        query: String,
        onlyFav: Boolean = false,
        pageSize: Int = 20
    ): Flow<PagingData<City>>

    suspend fun preloadCities()

    suspend fun toggleFavorite(cityId: Long)

    suspend fun getCityById(id: Long): City?

    suspend fun getCitiesCount(): Int
}