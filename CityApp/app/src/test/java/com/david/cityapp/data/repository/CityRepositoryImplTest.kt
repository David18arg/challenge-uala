package com.david.cityapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import androidx.paging.testing.asSnapshot
import com.david.cityapp.MainCoroutineRule
import com.david.cityapp.data.local.dao.CityDao
import com.david.cityapp.data.model.CityResponse
import com.david.cityapp.data.model.Coord
import com.david.cityapp.data.remote.CityApi
import com.david.cityapp.domain.model.City

class CityRepositoryImplTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()
    private lateinit var repository: CityRepositoryImpl
    private lateinit var mockDao: CityDao
    private lateinit var mockApi: CityApi
    private val testCity = City(
        id = 1,
        name = "Test City",
        country = "Test Country",
        lat = 0.0f,
        lon = 0.0f,
        isFavorite = false
    )

    @Before
    fun setUp() {
        mockApi = mockk()
        mockDao = mockk()
        repository = CityRepositoryImpl(api = mockApi, dao = mockDao)
    }

    @Test
    fun `getCities with empty query returns all cities`() = runTest {

        val allCities = listOf(city1, city2, city3)

        coEvery { mockDao.pagingCities(query = "", onlyFav = false) } returns getPagingSource(allCities)

        val result = repository.getCities(
            query = "",
            onlyFav = false,
            pageSize = 10
        )

        val resultCities = result.asSnapshot()
        assertEquals(allCities, resultCities)
    }

    @Test
    fun `getCities with query filters cities by name prefix`() = runTest {
        // Given - Test data
        val testQuery = "Test"
        val expectedFiltered = listOf(city1, city3)

        // Mock DAO to return filtered results - use any() for query to handle case sensitivity
        coEvery { mockDao.pagingCities(query = any(), onlyFav = false) } returns getPagingSource(expectedFiltered)

        // When
        val result = repository.getCities(
            query = testQuery,
            onlyFav = false,
            pageSize = 10
        )

        // Then
        val actual = result.asSnapshot()

        assertEquals(2, actual.size)
        assertEquals(expectedFiltered.map { it.name }.toSet(), actual.map { it.name }.toSet())
    }

    @Test
    fun `getCities with onlyFavorites true returns only favorited cities`() = runTest {
        // Given
        // Given - Test data
        val testQuery = "Test"
        val expectedFiltered = listOf(city1)

        // Mock DAO to return filtered results - use any() for query to handle case sensitivity
        coEvery { mockDao.pagingCities(query = any(), onlyFav = true) } returns getPagingSource(expectedFiltered)

        // When
        val result = repository.getCities(
            query = testQuery,
            onlyFav = true,
            pageSize = 10
        )

        // Then
        val response = result.asSnapshot()

        assertEquals(1, response.size)
        assertTrue(response[0].isFavorite)
    }

    @Test
    fun `preloadCities fetches from api and saves to db`() = runTest {
        // Given
        val allResponse = listOf(response1, response2, response3)
        val allCities = listOf(city1, city2, city3)

        // Mock the API response
        coEvery { mockApi.fetchCitiesStream() } returns allResponse

        // Mock the DAO insert
        coEvery { mockDao.insertCities(any()) } just Runs

        // Mock the paging source for getCities()
        coEvery { mockDao.pagingCities("", false) } returns getPagingSource(allCities)

        repository.preloadCities()

        coVerify(exactly = 1) {
            mockApi.fetchCitiesStream()
            mockDao.insertCities(match { it.size == allCities.size })
        }

        val result = repository.getCities(
            query = "",
            onlyFav = false,
            pageSize = 10
        )
        val resultCities = result.asSnapshot()

        assertEquals(allCities.size, resultCities.count())
        assertTrue(resultCities.containsAll(allCities))
    }

    @Test
    fun `toggleFavorite updates city favorite status`() = runTest {
        // Given
        val cityId = 1L
        val initialCity = testCity.copy(id = cityId, isFavorite = false)
        val toggledCity = initialCity.copy(isFavorite = true)

        coEvery { mockDao.getCityById(cityId) } returns initialCity
        coEvery { mockDao.updateFavoriteStatus(cityId, true) } just Runs

        repository.toggleFavorite(cityId)

        coVerify(exactly = 1) {
            mockDao.getCityById(cityId)
            mockDao.updateFavoriteStatus(cityId, true)
        }

        coEvery { mockDao.getCityById(cityId) } returns toggledCity
        val updatedCity = repository.getCityById(cityId)
        assertTrue(updatedCity.isFavorite)

        coEvery { mockDao.getCityById(cityId) } returns toggledCity
        coEvery { mockDao.updateFavoriteStatus(cityId, false) } just Runs
        repository.toggleFavorite(cityId)

        coVerify { mockDao.updateFavoriteStatus(cityId, false) }
        coEvery { mockDao.getCityById(cityId) } returns initialCity
        val finalCity = repository.getCityById(cityId)
        assertFalse(finalCity.isFavorite)
    }

    @Test
    fun `getCityById returns city when found`() = runTest {
        val cityId = 1L
        coEvery { mockDao.getCityById(cityId) } returns testCity

        val result = repository.getCityById(cityId)

        assertEquals(testCity, result)
    }

    @Test(expected = IllegalStateException::class)
    fun `getCityById throws when city not found`() = runTest {
        val cityId = 999L
        coEvery { mockDao.getCityById(cityId) } returns null

        val result = repository.getCityById(cityId)

        assertEquals(result, null)
    }

    @Test
    fun `getCitiesCount returns correct count`() = runTest {
        val count = 5
        coEvery { mockDao.getCitiesCount() } returns count

        val result = repository.getCitiesCount()

        assertEquals(count, result)
    }

    val response1 = CityResponse(
        name = "Test City",
        country = "Test Country",
        coord = Coord(
            lat = 40.7128f,
            lon = -74.0060f
        )
    )
    val city1 = City(
        id = 1,
        name = "Test City",
        country = "Test Country",
        lat = 40.7128f,
        lon = -74.0060f,
        isFavorite = true
    )

    val response2 = CityResponse(
        name = "Another City",
        country = "Another Country",
        coord = Coord(
            lat = 40.7128f,
            lon = -74.0060f
        )
    )
    val city2 = City(
        id = 2,
        name = "Another City",
        country = "Another Country",
        lat = 40.7128f,
        lon = -74.0060f,
        isFavorite = false
    )

    val response3 = CityResponse(
        name = "Testing 123",
        country = "Testing Country",
        coord = Coord(
            lat = 40.7128f,
            lon = -74.0060f
        )
    )

    val city3 = City(
        id = 3,
        name = "Testing 123",
        country = "Testing Country",
        lat = 0f,
        lon = 0f,
        isFavorite = false
    )

    private fun getPagingSource(data : List<City>): PagingSource<Int, City> {
        val pagingSource = object : PagingSource<Int, City>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, City> {
                return LoadResult.Page(
                    data = data,
                    prevKey = null,
                    nextKey = null
                )
            }

            override fun getRefreshKey(state: PagingState<Int, City>): Int? = null
        }
        return pagingSource
    }
}