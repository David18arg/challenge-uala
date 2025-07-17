package com.david.cityapp.data.repository

import com.david.cityapp.data.remote.CityApi
import com.david.cityapp.domain.model.City
import com.david.cityapp.domain.repository.CityRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val api: CityApi
) : CityRepository {
    override fun getCities(
        query: String,
        onlyFav: Boolean,
        pageSize: Int
    ): List<City> {
        TODO("Not yet implemented")
    }

    override suspend fun preloadCities() {
        TODO("Not yet implemented")
    }
}