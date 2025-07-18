package com.david.cityapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

import com.david.cityapp.domain.model.City

@Dao
interface CityDao {

    @Query("""
    SELECT * FROM cities
    WHERE (:onlyFav = 0 OR isFavorite = 1)
      AND name LIKE :query || '%' COLLATE NOCASE
    ORDER BY name COLLATE NOCASE
    """)
    fun pagingCities(query: String, onlyFav: Boolean): PagingSource<Int, City>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<City>)

    @Query("UPDATE cities SET isFavorite = :isFavorite WHERE id = :cityId")
    suspend fun updateFavoriteStatus(cityId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM cities WHERE id = :cityId")
    suspend fun getCityById(cityId: Long): City?

    @Query("SELECT COUNT(*) FROM cities")
    suspend fun getCitiesCount(): Int

    @Query("DELETE FROM cities")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(cities: List<City>) {
        clearAll()
        insertCities(cities)
    }
}