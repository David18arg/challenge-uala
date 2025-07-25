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
class ScreenContentTest {

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
    fun `weather content shows loading state when weather is null`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                ScreenContent(weather = null)
            }
        }

        // Then - Verify that no weather information is displayed
        composeTestRule.onNodeWithText("25°C").assertDoesNotExist()
    }

    @Test
    fun `weather content shows temperature when weather is provided`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                ScreenContent(weather = testWeather)
            }
        }

        // Then
        composeTestRule.onNodeWithText("25°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cielo claro").assertIsDisplayed()
    }

    @Test
    fun `weather content shows weather details when weather is provided`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                ScreenContent(weather = testWeather)
            }
        }

        // Then - Verify that weather details are displayed
        composeTestRule.onNodeWithText("27°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("5.0 m/s").assertIsDisplayed()
    }

    @Test
    fun `weather content shows all required components`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                ScreenContent(weather = testWeather)
            }
        }

        // Then - Verify that all required components are displayed
        composeTestRule.onNodeWithText("Sensación térmica").assertIsDisplayed()
        composeTestRule.onNodeWithText("27°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Viento").assertIsDisplayed()
        composeTestRule.onNodeWithText("5.0 m/s").assertIsDisplayed()
    }
}