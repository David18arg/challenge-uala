package com.david.cityapp.presentation.ui.screens.citymap.components

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
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

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [33],
    manifest = Config.NONE
)
@LooperMode(LooperMode.Mode.PAUSED)
class ScreenContentTest {

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

    private val geoPoint = GeoPoint(10.0f.toDouble(),20.0f.toDouble())

    @Test
    fun `city map content shows map container`() {

        composeTestRule.setContent {
            CityAppTheme {
                val context = LocalContext.current

                ScreenContent(
                    city = testCity,
                    mapCenter = geoPoint,
                    mapZoom = 15.0,
                    context = context,
                    onMapMoved = { _, _ -> }
                )
            }
        }

        // Then
        composeTestRule
            .onNode(hasTestTag("CityMapContent"))
            .assertExists()
    }

    @Test
    fun `city map content shows current card view`() {
        composeTestRule.setContent {
            CityAppTheme {
                val context = LocalContext.current
                Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

                ScreenContent(
                    city = testCity,
                    mapCenter = geoPoint,
                    mapZoom = 15.0,
                    context = context,
                    onMapMoved = { _, _ -> }
                )
            }
        }

        // Use a simple assertion that doesn't rely on the looper
        composeTestRule
            .onNode(hasTestTag("CardContent"))
            .assertExists()
    }
}
