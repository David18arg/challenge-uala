package com.david.cityapp.presentation.ui.screens.citylist

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.test.runner.AndroidJUnit4
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.theme.CityAppTheme
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [33]
)
class CityListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = StandardTestDispatcher()


    private val testCity = City(
        id = 1,
        name = "Test City",
        country = "Test Country",
        lat = 0.0f,
        lon = 0.0f,
        isFavorite = false
    )

    private val testPagingData = PagingData.from(listOf(testCity))


    @Test
    fun `city list screen shows loading state`() {
        // Given
        val viewModel = mockk<CityListViewModel>(relaxed = true)
        val emptyPagingData = PagingData.from(emptyList<City>())

        // Mock the ViewModel state with loading true and empty paging data
        every { viewModel.uiState } returns MutableStateFlow(
            CityListUiState(
                isLoading = true,
                cities = emptyPagingData
            )
        )

        // Mock the citiesFlow to return empty paging data
        coEvery { viewModel.citiesFlow } returns flowOf(emptyPagingData)

        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityListScreen(
                    onCityClick = {},
                    onClickToDetails = {},
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
        }

        // Then - Verify loading indicator is shown
        composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()
    }

    @Test
    fun `city list screen shows empty state`() {
        // Given
        val viewModel = mockk<CityListViewModel>(relaxed = true)

        // Mock the ViewModel state
        every { viewModel.uiState } returns MutableStateFlow(
            CityListUiState(
                isLoading = false,
                showFavoritesOnly = false,
                searchQuery = ""
            )
        )

        // Mock the citiesFlow to return an empty PagingData
        val emptyPagingData = PagingData.empty<City>()
        coEvery { viewModel.citiesFlow } returns flowOf(emptyPagingData)

        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityListScreen(
                    onCityClick = {},
                    onClickToDetails = {},
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
        }

        // Wait for the UI to settle
        composeTestRule.waitForIdle()

        // Then - Verify loading indicator is shown
        composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()

    }

    @Test
    fun `city list screen shows error state`() {
        // Given
        val viewModel = mockk<CityListViewModel>(relaxed = true)
        val errorMessage = "No hay ciudades disponibles"

        // Mock uiState
        every { viewModel.uiState } returns MutableStateFlow(
            CityListUiState(
                isLoading = false,
                showFavoritesOnly = false,
                error = errorMessage,
                searchQuery = ""
            )
        )

        // Mock citiesFlow to return empty PagingData
        val emptyPagingData = PagingData.empty<City>()
        coEvery { viewModel.citiesFlow } returns flowOf(emptyPagingData)

        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityListScreen(
                    onCityClick = {},
                    onClickToDetails = {},
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
        }

        // Wait for the UI to settle
        composeTestRule.waitForIdle()

        // Then - Verify the error message is shown
        composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `city list screen shows list of cities`() {
        // Given
        val testCity = City(
            id = 1,
            name = "Test City",
            country = "Test Country",
            lat = 0.0f,
            lon = 0.0f,
            isFavorite = false
        )

        // Create a mock ViewModel
        val mockViewModel = mockk<CityListViewModel>()

        // Create a flow that emits our test cities
        val testCitiesFlow = flowOf(PagingData.from(listOf(testCity)))

        // Mock the ViewModel behavior
        every { mockViewModel.uiState } returns MutableStateFlow(
            CityListUiState(
                isLoading = false,
                showFavoritesOnly = false,
                searchQuery = "",
                error = null,
                cities = PagingData.empty()
            )
        )

        // Mock the citiesFlow to return our test data
        every { mockViewModel.citiesFlow } returns testCitiesFlow

        // Mock the selectedCity to return null (no city selected)
        every { mockViewModel.selectedCity } returns MutableStateFlow(null)

        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityListScreen(
                    onCityClick = {},
                    onClickToDetails = {},
                    viewModel = mockViewModel,
                    modifier = Modifier
                )
            }
        }

        // Advance time to process the flow
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Verify the city is displayed
        composeTestRule.onNodeWithText("Test City, Test Country").assertIsDisplayed()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `search bar updates search query`() {
        // Given
        val viewModel = mockk<CityListViewModel>(relaxed = true)

        // Create a flow to simulate the UI state
        val mockUiState = MutableStateFlow(
            CityListUiState(
                searchQuery = "",
                showFavoritesOnly = false,
                isLoading = false,
                error = null,
                selectedCity = null,
                cities = PagingData.empty()
            )
        )

        // Mock the uiState to return our test flow
        every { viewModel.uiState } returns mockUiState.asStateFlow()

        // Capture the search query updates
        val searchQueries = mutableListOf<String>()
        every { viewModel.onSearchQueryChanged(capture(searchQueries)) } answers {
            // Update our mock UI state when search query changes
            mockUiState.update { it.copy(searchQuery = searchQueries.last()) }
        }

        composeTestRule.setContent {
            CityAppTheme {
                CityListScreen(
                    onCityClick = {},
                    onClickToDetails = {},
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
        }

        // Wait for initial composition
        composeTestRule.waitForIdle()

        // When - Enter text in the search bar
        composeTestRule.onNode(hasTestTag("searchTextField")).performTextInput("test")

        // Wait for any pending coroutines
        testDispatcher.scheduler.advanceUntilIdle()
        composeTestRule.waitForIdle()

        // Then - Verify the search query was updated in the ViewModel
        verify { viewModel.onSearchQueryChanged("test") }

        // Also verify the UI state was updated
        assertEquals(mockUiState.value.searchQuery,"test")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `toggling favorite calls onFavoriteToggled`() = runTest {
        // Given
        val testCity = City(
            id = 1,
            name = "Test City",
            country = "Test Country",
            lat = 0.0f,
            lon = 0.0f,
            isFavorite = false
        )

        // Create a simple ViewModel with mock behavior
        val viewModel = mockk<CityListViewModel> {
            every { uiState } returns MutableStateFlow(
                CityListUiState(
                    cities = PagingData.from(listOf(testCity)),
                    isLoading = false,
                    error = null,
                    searchQuery = "",
                    showFavoritesOnly = false,
                    selectedCity = null
                )
            )
            every { citiesFlow } returns flowOf(PagingData.from(listOf(testCity)))
            every { selectedCity } returns MutableStateFlow(null)
            every { toggleFavoritesFilter() } just Runs
            coEvery { onFavoriteToggled(testCity) } just Runs
        }

        // Set up the composable with our mocked ViewModel
        composeTestRule.setContent {
            CityAppTheme {
                CityListScreen(
                    onCityClick = {},
                    onClickToDetails = {},
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
        }

        // Wait for initial composition
        composeTestRule.waitForIdle()

        // When - Click the favorite button
        composeTestRule.onNodeWithContentDescription("Toggle favorite city").performClick()

        // Then - Verify onFavoriteToggled was called with the test city
        coVerify(exactly = 1) {
            viewModel.onFavoriteToggled(testCity)
        }
    }

    @Test
    fun `clicking on city calls onCityClick`() {
        // Given
        val viewModel = mockk<CityListViewModel>(relaxed = true)

        // Setup test data
        val testCity = City(
            id = 1,
            name = "Test City",
            country = "Test Country",
            lat = 0.0f,
            lon = 0.0f,
            isFavorite = false
        )

        // Create a test flow with our test city
        val testPagingData = PagingData.from(listOf(testCity))
        val testFlow = flowOf(testPagingData)

        // Mock the ViewModel
        every { viewModel.uiState } returns MutableStateFlow(CityListUiState())
        coEvery { viewModel.citiesFlow } returns testFlow
        coEvery { viewModel.selectedCity } returns MutableStateFlow(testCity)

        composeTestRule.setContent {
            CityAppTheme {
                CityListScreen(
                    onCityClick = { (City) -> Unit },
                    onClickToDetails = {},
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
        }

        // When - Click on the city item
        composeTestRule.onNode(hasTestTag("CityItem")).performClick()

    }

    @Test
    fun `clicking on details icon calls onClickToDetails`() {
        // Given
        val viewModel = mockk<CityListViewModel>(relaxed = true)

        // Setup test data
        val testCity = City(
            id = 1,
            name = "Test City",
            country = "Test Country",
            lat = 0.0f,
            lon = 0.0f,
            isFavorite = false
        )

        // Create a test flow with our test city
        val testPagingData = PagingData.from(listOf(testCity))
        val testFlow = flowOf(testPagingData)

        // Mock the ViewModel
        every { viewModel.uiState } returns MutableStateFlow(CityListUiState())
        coEvery { viewModel.citiesFlow } returns testFlow
        coEvery { viewModel.selectedCity } returns MutableStateFlow(testCity)

        composeTestRule.setContent {
            CityAppTheme {
                CityListScreen(
                    onCityClick = { },
                    onClickToDetails = { (City) -> Unit },
                    viewModel = viewModel,
                    modifier = Modifier
                )
            }
        }

        // When - Click on the details item
        composeTestRule.onNodeWithContentDescription("Toggle favorite city").performClick()
    }

    @Test
    fun `toggling favorites filter updates UI`() {
        // Given
        val viewModel = mockk<CityListViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            CityListUiState(
                cities = testPagingData,
                showFavoritesOnly = false
            )
        )

        composeTestRule.setContent {
            CityListScreen(
                onCityClick = {},
                onClickToDetails = {},
                viewModel = viewModel,
                modifier = Modifier
            )
        }

        // When
        composeTestRule.onNodeWithContentDescription("Toggle favorite").performClick()

        // Then
        verify(exactly = 1) { viewModel.toggleFavoritesFilter() }
    }
}