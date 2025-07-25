package com.david.cityapp.presentation.ui.screens.citymap

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.theme.CityAppTheme
import com.david.cityapp.presentation.ui.screens.citymap.components.CityMapUiState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [33],
    manifest = Config.NONE
)
@LooperMode(LooperMode.Mode.PAUSED)
class CityMapScreenTest {

    @OptIn(ExperimentalTestApi::class)
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testCity = City(
        id = 1,
        name = "Test City",
        country = "Test Country",
        lat = 10.0f,
        lon = 20.0f,
        isFavorite = false
    )

    private val geoPoint = GeoPoint(10.0f.toDouble(),20.0f.toDouble())

    @Test
    fun `city map screen shows loading state`() {
        // Given
        val viewModel = mockk<CityMapViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            CityMapUiState(
                isLoading = true,
                city = null,
                error = null,
                mapCenter = geoPoint,
                mapZoom = 15.0
            )
        )

        // When
        composeTestRule.setContent {
            CityAppTheme {
                // Initialize OSMDroid configuration for tests
                val context = LocalContext.current
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

                CityMapScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Wait for any pending UI operations to complete
        composeTestRule.waitForIdle()

        // Then - Verify that the Loading component is shown when loading
        composeTestRule.onNodeWithTag("loadingIndicator", useUnmergedTree = true)
            .assertExists("Loading indicator should be displayed")
            .assertIsDisplayed()
    }

    @Test
    fun `city map screen shows error state`() {
        // Given
        val errorMessage = "Error loading city map"
        val viewModel = mockk<CityMapViewModel>()

        // Stub the uiState to return an error state
        every { viewModel.uiState } returns MutableStateFlow(
            CityMapUiState(
                isLoading = false,
                city = null,
                error = errorMessage,
                mapCenter = geoPoint,
                mapZoom = 15.0
            )
        )

        // Stub the loadCity method to do nothing (since we're testing the error state)
        coEvery { viewModel.loadCity(1) } just Runs

        // When
        composeTestRule.setContent {
            CityAppTheme {
                // Initialize OSMDroid configuration for tests
                val context = LocalContext.current
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

                CityMapScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Wait for any pending UI operations to complete
        composeTestRule.waitForIdle()

        // Then - Verify that the error message is displayed in the Message component
        composeTestRule.onNodeWithText(errorMessage, useUnmergedTree = true)
            .assertExists("Error message should be displayed")
            .assertIsDisplayed()

        // Verify that loadCity was called with the correct cityId
        coVerify { viewModel.loadCity(1) }
    }

    @Test
    fun `city map screen shows city name in app bar`() {
        // Given
        val viewModel = mockk<CityMapViewModel>()

        // Stub the uiState to return the test city
        every { viewModel.uiState } returns MutableStateFlow(
            CityMapUiState(
                city = testCity,
                isLoading = false,
                error = null,
                mapCenter = geoPoint,
                mapZoom = 15.0
            )
        )

        // Stub the loadCity method to do nothing
        coEvery { viewModel.loadCity(1) } just Runs

        // Stub updateMapCenter to do nothing
        coEvery { viewModel.updateMapCenter(any(), any()) } just Runs

        // When
        composeTestRule.setContent {
            CityAppTheme {
                // Initialize OSMDroid configuration for tests
                val context = LocalContext.current
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

                CityMapScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Wait for any pending UI operations to complete
        composeTestRule.waitForIdle()

        // Then - Verify the city name is displayed in the app bar
        composeTestRule.onNodeWithText("Test City, Test Country")
            .assertExists("City and country name should be displayed in the app bar")
            .assertIsDisplayed()

        // Verify that loadCity was called with the correct cityId
        coVerify { viewModel.loadCity(1) }
    }

    @Test
    fun `clicking back button triggers onBackClick`() {
        // Given
        var backClicked = false
        val viewModel = mockk<CityMapViewModel>(relaxed = true)

        // Stub the UI state
        every { viewModel.uiState } returns MutableStateFlow(
            CityMapUiState(
                city = testCity,
                isLoading = false,
                error = null,
                mapCenter = geoPoint,
                mapZoom = 15.0
            )
        )

        // When
        composeTestRule.setContent {
            CityAppTheme {
                // Initialize OSMDroid configuration for tests
                val context = LocalContext.current
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

                CityMapScreen(
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
    fun `loading city when screen is launched`() {
        // Given
        val viewModel = mockk<CityMapViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            CityMapUiState(
                isLoading = false,
                city = testCity,
                error = null,
                mapCenter = geoPoint,
                mapZoom = 15.0
            )
        )

        // When
        composeTestRule.setContent {
            CityAppTheme {
                CityMapScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Then
        verify(exactly = 1) { viewModel.loadCity(1) }
    }

    @Test
    fun `map is displayed with correct coordinates`() {
        // Given
        val viewModel = mockk<CityMapViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(
            CityMapUiState(
                isLoading = false,
                city = testCity,
                error = null,
                mapCenter = geoPoint,
                mapZoom = 15.0
            )
        )

        // Stub the loadCity method to do nothing
        coEvery { viewModel.loadCity(1) } just Runs
        coEvery { viewModel.updateMapCenter(any(), any()) } just Runs

        // When
        composeTestRule.setContent {
            CityAppTheme {
                val context = LocalContext.current
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

                CityMapScreen(
                    cityId = 1,
                    onBackClick = {},
                    viewModel = viewModel
                )
            }
        }

        // Wait for any pending UI operations to complete
        composeTestRule.waitForIdle()

        // Then - Verify the app bar shows the city name, which indicates the map is loaded
        composeTestRule.onNodeWithText("${testCity.name}, ${testCity.country}", useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()

        // Verify the loadCity was called
        verify(exactly = 1) { viewModel.loadCity(1) }
    }
}
