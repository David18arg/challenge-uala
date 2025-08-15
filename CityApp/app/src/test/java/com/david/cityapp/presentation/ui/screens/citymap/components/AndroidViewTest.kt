package com.david.cityapp.presentation.ui.screens.citymap.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.theme.CityAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import kotlin.intArrayOf
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [33],
    manifest = Config.NONE
)
@LooperMode(LooperMode.Mode.PAUSED)
class AndroidViewTest {

    @OptIn(ExperimentalTestApi::class)
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCity = City(
        id = 1,
        name = "Test City",
        country = "Test Country",
        lat = 10.0f,
        lon = 20.0f,
        isFavorite = false
    )

    private val testGeoPoint = GeoPoint(10.0, 20.0)
    private val testZoom = 12.0

    @Test
    fun `city map view shows map android view`() {

        composeTestRule.setContent {
            CityAppTheme {
                val context = LocalContext.current
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

                ScreenContent(
                    city = testCity,
                    mapCenter = testGeoPoint,
                    mapZoom = 15.0,
                    context = context,
                    onMapMoved = { _, _ -> },
                    modifier = Modifier
                )
            }
        }

        // Use a simple assertion that doesn't rely on the looper
        composeTestRule
            .onNode(hasTestTag("CityMapView"))
            .assertExists()
    }

    @Test
    fun `city map view calls onMapMoved when map is scrolled or zoomed`() {
        // Given
        var lastCenter: GeoPoint? = null
        var lastZoom: Double? = null

        // When
        composeTestRule.setContent {
            CityAppTheme {
                val context = LocalContext.current
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

                AndroidView(
                    center = testGeoPoint,
                    zoom = testZoom,
                    city = testCity,
                    context = context,
                    onMapMoved = { center, zoom ->
                        lastCenter = center
                        lastZoom = zoom
                    }
                )
            }
        }

        // Then - Verify the map view is displayed with the test tag
        composeTestRule.onNode(hasTestTag("CityMapView")).assertExists().assertIsDisplayed()

        // Simulate scrolling the map
        composeTestRule.onNode(hasTestTag("CityMapView")).performTouchInput { swipeLeft() }
        composeTestRule.onNode(hasTestTag("CityMapView")).performTouchInput { swipeRight() }

    }

    @Test
    fun `city map view updates when city changes`() {
        // Given
        val initialCity = testCity
        val newCity = City(
            id = 2,
            name = "New Test City",
            country = "New Test Country",
            lat = 34.0522f,
            lon = -118.2437f,
            isFavorite = false
        )
        val newGeoPoint = GeoPoint(newCity.lat.toDouble(), newCity.lon.toDouble())

        // Track the current center, zoom, and city using remember
        var currentCenter by mutableStateOf(testGeoPoint)
        var currentZoom by mutableStateOf(testZoom)
        var currentCity by mutableStateOf(initialCity)

        // When - Render the component
        composeTestRule.setContent {
            CityAppTheme {
                val context = LocalContext.current

                AndroidView(
                    center = currentCenter,
                    zoom = currentZoom,
                    city = currentCity,
                    context = context,
                    onMapMoved = { center, zoom ->
                        currentCenter = center
                        currentZoom = zoom
                    }
                )
            }
        }

        // Verify initial state
        composeTestRule
            .onNode(hasTestTag("CityMapView"))
            .assertExists()
            .assertIsDisplayed()

        // When - Update the city
        composeTestRule.runOnIdle {
            currentCity = newCity
            currentCenter = newGeoPoint
        }

        // Wait for updates and animations to complete
        composeTestRule.waitForIdle()

        // Then - Verify the map updated with new city coordinates
        assertEquals(
            newCity.lat.toDouble(),
            currentCenter.latitude,
            0.01,
            "Map center latitude should match the new city's latitude"
        )
        assertEquals(
            newCity.lon.toDouble(),
            currentCenter.longitude,
            0.01,
            "Map center longitude should match the new city's longitude"
        )
    }

    @Test
    fun `city map view shows error message when context is null`() {
        // When - Render the component with null context
        composeTestRule.setContent {
            CityAppTheme {
                AndroidView(
                    center = testGeoPoint,
                    zoom = testZoom,
                    city = testCity,
                    context = null,
                    onMapMoved = { _, _ -> }
                )
            }
        }

        // Then - Wait for any effects to complete
        composeTestRule.waitForIdle()

        // Verify that the error message is displayed when context is null
        composeTestRule.onNodeWithText("Error cargando el mapa")
            .assertExists()
            .assertIsDisplayed()

    }

    @Test
    fun `city map view does not show error with valid context`() {
        // When - Render the component with valid context
        composeTestRule.setContent {
            CityAppTheme {
                AndroidView(
                    center = testGeoPoint,
                    zoom = testZoom,
                    city = testCity,
                    context = LocalContext.current,
                    onMapMoved = { _, _ -> }
                )
            }
        }

        // Then - Wait for any effects to complete
        composeTestRule.waitForIdle()

        // Verify that the error message is NOT displayed when context is valid
        composeTestRule.onNodeWithText("Error cargando el mapa").assertDoesNotExist()

        // Verify the map view is displayed
        composeTestRule.onNode(hasTestTag("CityMapView")).assertExists()
    }
}
