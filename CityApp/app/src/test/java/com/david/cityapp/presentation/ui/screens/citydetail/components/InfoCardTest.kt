package com.david.cityapp.presentation.ui.screens.citydetail.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.david.cityapp.data.model.Clouds
import com.david.cityapp.data.model.Coordinates
import com.david.cityapp.data.model.MainWeatherData
import com.david.cityapp.data.model.SystemData
import com.david.cityapp.data.model.WeatherDescription
import com.david.cityapp.data.model.Wind
import com.david.cityapp.domain.model.Weather
import com.david.cityapp.presentation.common.theme.CityAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [33]
)
class InfoCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
    fun `weather info card shows title`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                InfoCard(weather = testWeather)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Detalles del clima").assertIsDisplayed()
    }

    @Test
    fun `weather info card shows all weather details`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                InfoCard(weather = testWeather)
            }
        }

        // Then - Verify all weather details are displayed
        // Left column items
        composeTestRule.onNodeWithText("Sensación térmica").assertIsDisplayed()
        composeTestRule.onNodeWithText("27°C").assertIsDisplayed()

        composeTestRule.onNodeWithText("Humedad").assertIsDisplayed()
        composeTestRule.onNodeWithText("65%").assertIsDisplayed()

        composeTestRule.onNodeWithText("Presión").assertIsDisplayed()
        composeTestRule.onNodeWithText("1012 hPa").assertIsDisplayed()

        // Right column items
        composeTestRule.onNodeWithText("Viento").assertIsDisplayed()
        composeTestRule.onNodeWithText("5.0 m/s").assertIsDisplayed()

        composeTestRule.onNodeWithText("Visibilidad").assertIsDisplayed()
        composeTestRule.onNodeWithText("10 km").assertIsDisplayed()

        composeTestRule.onNodeWithText("Nubosidad").assertIsDisplayed()
        composeTestRule.onNodeWithText("0%").assertIsDisplayed()
    }

    @Test
    fun `weather info card handles missing data gracefully`() {
        // Given - Weather object with some null/zero values
        val incompleteWeather = testWeather.copy(
            main = testWeather.main.copy(humidity = 0),
            visibility = 0,
            clouds = Clouds(all = 0)
        )

        // When
        composeTestRule.setContent {
            CityAppTheme {
                InfoCard(weather = incompleteWeather)
            }
        }

        // Then - Verify the component still renders without errors
        composeTestRule.onNodeWithText("Humedad").assertIsDisplayed()

        composeTestRule.onNodeWithText("%").assertDoesNotExist()

        composeTestRule.onNodeWithText("Visibilidad").assertIsDisplayed()
        composeTestRule.onNodeWithText("km").assertDoesNotExist()

        composeTestRule.onNodeWithText("Nubosidad").assertIsDisplayed()
        composeTestRule.onNodeWithText("%").assertDoesNotExist()
    }
}