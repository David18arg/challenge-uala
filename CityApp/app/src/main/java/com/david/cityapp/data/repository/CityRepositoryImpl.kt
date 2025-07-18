package com.david.cityapp.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.david.cityapp.data.local.dao.CityDao
import com.david.cityapp.data.model.toDomain
import com.david.cityapp.data.remote.CityApi
import com.david.cityapp.domain.model.City
import com.david.cityapp.domain.repository.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepositoryImpl @Inject constructor(
    private val dao: CityDao,
    private val api: CityApi
) : CityRepository {
    override fun getCities(
        query: String,
        onlyFav: Boolean,
        pageSize: Int
    ): Flow<PagingData<City>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                prefetchDistance = pageSize * 2,
                initialLoadSize = pageSize * 3
            ),
            pagingSourceFactory = {
                dao.pagingCities(
                    query = query.lowercase(),
                    onlyFav = onlyFav
                )
            }
        ).flow
    }

    override suspend fun preloadCities(): Unit = withContext(Dispatchers.IO) {
        val citiesResponse = api.fetchCitiesStream()
        val cities = citiesResponse.map { it.toDomain() }
        dao.insertCities(cities)
    }

    override suspend fun toggleFavorite(cityId: Long) {
        val current = dao.getCityById(cityId)?.isFavorite ?: return
        dao.updateFavoriteStatus(cityId, !current)
    }

    override suspend fun getCityById(id: Long): City {
        return dao.getCityById(id) ?: throw IllegalStateException("City not found")
    }

    override suspend fun getCitiesCount(): Int {
        return dao.getCitiesCount()
    }
}
