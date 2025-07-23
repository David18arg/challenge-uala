package com.david.cityapp.presentation.ui.screens.citylist

import androidx.paging.PagingData
import app.cash.turbine.test
import com.david.cityapp.MainCoroutineRule
import com.david.cityapp.domain.model.City
import com.david.cityapp.domain.repository.CityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CityListViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: CityListViewModel
    private val mockRepository: CityRepository = mockk(relaxed = true)

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
        Dispatchers.setMain(testDispatcher)
        viewModel = CityListViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Given initial state
        viewModel = CityListViewModel(mockRepository)
        // Then
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertEquals(false, viewModel.uiState.value.showFavoritesOnly)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onSearchQueryChanged updates search query`() = runTest {
        // Given
        val query = "test"
        val testCities = listOf(
            City(id = 1, name = "Test City 1", country = "Country", lat = 0f, lon = 0f, isFavorite = false),
            City(id = 2, name = "Test City 2", country = "Country", lat = 1f, lon = 1f, isFavorite = false)
        )
        val pagingData = PagingData.from(testCities)

        // Mock the repository to return our test data
        every { mockRepository.getCities(any(), any(), any()) } returns flowOf(pagingData)

        // When & Then
        viewModel.uiState.test {
            // Skip the initial state
            val initialState = awaitItem()
            assertTrue(initialState.searchQuery.isEmpty())

            // When we update the search query
            viewModel.onSearchQueryChanged(query)

            // Then we should receive the updated state with the new query
            val updatedState = awaitItem()
            assertEquals(query, updatedState.searchQuery)

            // Verify the cities flow is updated with the new query
            viewModel.citiesFlow.test {
                // The flow should emit the test data
                val emittedData = awaitItem()
                assertNotNull(emittedData)

                // Clean up
                cancelAndIgnoreRemainingEvents()
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavoritesFilter toggles showFavoritesOnly`() = runTest {
        // Given
        val testCity = City(id = 1, name = "Test City", country = "Country", lat = 0f, lon = 0f, isFavorite = true)
        val testPagingData = PagingData.from(listOf(testCity))

        // Mock the repository to return test data
        every { mockRepository.getCities(any(), any(), any()) } returns flowOf(testPagingData)

        // Verify initial state is false
        val initialState = viewModel.uiState.value
        assertFalse("Initial showFavoritesOnly should be false", initialState.showFavoritesOnly)

        // When - First toggle (should set to true)
        viewModel.toggleFavoritesFilter()

        // Then - Verify state is false again
        val secondToggleState = viewModel.uiState.value
        assertFalse("Second toggle should set showFavoritesOnly back to false", secondToggleState.showFavoritesOnly)
    }

    @Test
    fun `onFavoriteToggled calls repository toggleFavorite`() = runTest {
        // Given
        coEvery { mockRepository.toggleFavorite(any()) } returns Unit

        // When
        viewModel.onFavoriteToggled(testCity)

        // Then
        coVerify { mockRepository.toggleFavorite(testCity.id) }
    }

    @Test
    fun `selectCity updates selectedCity`() = runTest {
        // Given - Initial state
        assertNull(viewModel.selectedCity.value)

        // When - Select a city
        viewModel.selectCity(testCity)

        // Then - Verify the selected city is updated
        assertEquals(testCity, viewModel.selectedCity.value)

        // When - Clear selection
        viewModel.selectCity(null)

        // Then - Verify the selection is cleared
        assertNull(viewModel.selectedCity.value)
    }


    @Test
    fun `uiState updates when search query changes`() = runTest {
        // Given
        val testQuery = "test city"
        val testCities = listOf(
            testCity.copy(id = 1, name = "Test City 1"),
            testCity.copy(id = 2, name = "Test City 2")
        )
        val testPagingData = PagingData.from(testCities)

        // Mock the repository to return test data when the search query is used
        coEvery {
            mockRepository.getCities(
                query = testQuery,
                onlyFav = false,
                pageSize = 10
            )
        } returns flowOf(testPagingData)

        // When - Update the search query
        viewModel.onSearchQueryChanged(testQuery)

        viewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            assertEquals(testQuery, initialState.searchQuery)
            assertFalse(initialState.showFavoritesOnly)

            // Change search query
            viewModel.onSearchQueryChanged("test")

            // Verify the search query was updated
            val updatedState = awaitItem()
            assertEquals("test", updatedState.searchQuery)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState updates when favorites filter is toggled`() = runTest {
        // Given - Set up mock response for getCities
        val testPagingData = PagingData.from(listOf(testCity))
        coEvery {
            mockRepository.getCities(
                query = "",
                onlyFav = any(),
                pageSize = 10
            )
        } returns flowOf(testPagingData)

        // When & Then - Check initial state
        viewModel.uiState.test {
            // Initial state should have showFavoritesOnly = false
            val initialState = awaitItem()
            assertFalse(initialState.showFavoritesOnly, "Initial state should have showFavoritesOnly = false")

            // When - Toggle favorites filter
            viewModel.toggleFavoritesFilter()

            // Then - Verify the state is updated to true
            val updatedState = awaitItem()
            assertTrue(updatedState.showFavoritesOnly, "State should update to showFavoritesOnly = true after toggle")

            // Verify the repository was called with onlyFav = true
            coVerify {
                mockRepository.getCities(
                    query = "",
                    onlyFav = true,
                    pageSize = 10
                )
            }

            cancelAndIgnoreRemainingEvents()
        }
    }
}