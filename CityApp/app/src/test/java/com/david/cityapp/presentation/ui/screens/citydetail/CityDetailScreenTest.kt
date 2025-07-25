package com.david.cityapp.presentation.ui.screens.citydetail

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.david.cityapp.data.model.Clouds
import com.david.cityapp.data.model.Coordinates
import com.david.cityapp.data.model.MainWeatherData
import com.david.cityapp.data.model.SystemData
import com.david.cityapp.data.model.WeatherDescription
import com.david.cityapp.data.model.Wind
import com.david.cityapp.domain.model.City
import com.david.cityapp.domain.model.Weather
import com.david.cityapp.presentation.common.theme.CityAppTheme
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [33]
)
class CityDetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testCity = City(
        id = 1,
        name = "Test City",
        country = "Test Country",
        lat = 0.0f,
        lon = 0.0f,
        isFavorite = false
    )

    private val testWeather = Weather(
        coord = Coordinates(
            lon = -58.38,
            lat = -34.6
        ),
        weather = listOf(
            WeatherDescription(
                id = 800,
                main = "Clear",
                description = "cielo claro",
                icon = "01d"
            )
        ),
        base = "stations",
        main = MainWeatherData(
            temp = 25.0,
            feelsLike = 27.0,
            tempMin = 22.0,
            tempMax = 28.0,
            pressure = 1012,
            humidity = 65,
            seaLevel = 1012,
            groundLevel = 1000,
            tempKf = 1.0
        ),
        visibility = 10000,
        wind = Wind(
            speed = 5.0,
            deg = 200,
            gust = 7.0
        ),
        clouds = Clouds(all = 0),
        dt = 1625000000,
        sys = SystemData(
            type = 1,
            id = 8224,
            country = "AR",
            sunrise = 1624946400,
            sunset = 1624984800
        ),
        timezone = -10800,
        id = 3433955,
        name = "Buenos Aires",
        cod = 200
    )

    @Test
    fun `city detail screen shows loading state`() {
        // Given
        val viewModel = mockk<CityDetailViewModel>(relaxed = true)

        // Initial state with loading true and no weather data
        val loadingState = CityDetailUiState(
            city = null,
            weather = null,
            isLoading = true,
            isFavorite = false,
            error = null
        )

        // Mock the state flow to return our test state
        every { viewModel.uiState } returns MutableStateFlow(loadingState)

        // When - Render the screen
        composeTestRule.setContent {
            CityAppTheme {
                CityDetailScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Wait for any pending UI operations to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithTag("loadingIndicator")
            .assertExists("Loading indicator should be displayed when loading with no weather data")
            .assertIsDisplayed()
    }

    @Test
    fun `city detail screen shows error state`() {
        // Given
        val errorMessage = "Error loading city details"
        val viewModel = mockk<CityDetailViewModel>(relaxed = true)

        val errorState = CityDetailUiState(
            city = null,
            weather = null,
            isLoading = false,
            isFavorite = false,
            error = errorMessage
        )
        every { viewModel.uiState } returns MutableStateFlow(errorState)


        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityDetailScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Wait for any pending UI operations to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun `city detail screen shows city and weather info`() {
        // Given
        val viewModel = mockk<CityDetailViewModel>(relaxed = true)

        val state = CityDetailUiState(
            isLoading = false,
            city = testCity,
            weather = testWeather,
            error = null
        )
        every { viewModel.uiState } returns MutableStateFlow(state)

        // Stub the loadCity method to do nothing (since we're testing the error state)
        coEvery { viewModel.loadCity(1) } just Runs

        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityDetailScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Wait for any pending UI operations to complete
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText("Test City, Test Country").assertIsDisplayed()
        composeTestRule.onNodeWithText("25°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cielo claro").assertIsDisplayed()
    }

    @Test
    fun `clicking back button triggers onBackClick`() {
        // Given
        var backClicked = false
        val viewModel = mockk<CityDetailViewModel>(relaxed = true)

        val state = CityDetailUiState(
            isLoading = false,
            city = testCity,
            weather = testWeather,
            error = null
        )
        every { viewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityDetailScreen(
                    cityId = 1,
                    onBackClick = { backClicked = true },
                    viewModel = viewModel
                )
            }
        }

        // Wait for any pending UI operations to complete
        composeTestRule.waitForIdle()

        // Then - Click the back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verify the callback was triggered
        assert(backClicked)
    }

    @Test
    fun `loading city details when screen is launched`() {
        // Given
        val viewModel = mockk<CityDetailViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            CityDetailUiState(
                isLoading = false,
                city = testCity,
                weather = testWeather,
                error = null
            )
        )

        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityDetailScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Then
        verify(exactly = 1) { viewModel.loadCity(1) }
    }
}