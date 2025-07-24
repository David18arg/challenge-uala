package com.david.cityapp.presentation.ui.screens.citylist.components

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.theme.CityAppTheme
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
class CityItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCity = City(
        id = 1,
        name = "Test City",
        country = "Test Country",
        lat = 0.0f,
        lon = 0.0f,
        isFavorite = false
    )

    @Test
    fun `city item displays city name and country`() {
        composeTestRule.setContent {
            CityAppTheme {
                Surface {
                    CityItem(
                        city = testCity,
                        onToggleFavorite = {},
                        onClick = {},
                        onClickToDetails = {}
                    )
                }
            }
        }

        // Verify city name and country are displayed
        composeTestRule.onNodeWithText("Test City, Test Country").assertIsDisplayed()
    }

    @Test
    fun `city item shows favorite icon`() {
        composeTestRule.setContent {
            CityAppTheme {
                Surface {
                    CityItem(
                        city = testCity,
                        onToggleFavorite = {},
                        onClick = {},
                        onClickToDetails = {}
                    )
                }
            }
        }

        // Verify favorite icon is displayed
        composeTestRule.onNodeWithContentDescription("Toggle favorite city").assertIsDisplayed()
    }

    @Test
    fun `clicking city item triggers onClick callback`() {
        var clickedCity: City? = null

        composeTestRule.setContent {
            CityAppTheme {
                Surface {
                    CityItem(
                        city = testCity,
                        onToggleFavorite = {},
                        onClick = { clickedCity = it },
                        onClickToDetails = {}
                    )
                }
            }
        }

        // Click on the city item
        composeTestRule.onNodeWithText("Test City, Test Country").performClick()

        // Verify the callback was called with the correct city
        assertEquals(testCity, clickedCity)
    }

    @Test
    fun `clicking favorite icon triggers onFavoriteClick callback`() {
        var favoriteToggled = false

        composeTestRule.setContent {
            CityAppTheme {
                Surface {
                    CityItem(
                        city = testCity,
                        onToggleFavorite = { favoriteToggled = true },
                        onClick = {},
                        onClickToDetails = {}
                    )
                }
            }
        }

        // Click on the favorite icon
        composeTestRule.onNodeWithContentDescription("Toggle favorite city").performClick()

        // Verify the callback was called
        assertEquals(true, favoriteToggled)
    }

    @Test
    fun `clicking details icon triggers onDetailsClick callback`() {
        var detailsClicked = false

        composeTestRule.setContent {
            CityAppTheme {
                Surface {
                    CityItem(
                        city = testCity,
                        onToggleFavorite = {},
                        onClick = {},
                        onClickToDetails = { detailsClicked = true }
                    )
                }
            }
        }

        // Click on the details icon
        composeTestRule.onNodeWithContentDescription("Toggle details city").performClick()

        // Verify the callback was called
        assertEquals(true, detailsClicked)
    }

    @Test
    fun `favorite icon reflects city favorite status`() {
        val favoritedCity = testCity.copy(isFavorite = true)

        composeTestRule.setContent {
            CityAppTheme {
                Surface {
                    CityItem(
                        city = favoritedCity,
                        onToggleFavorite = {},
                        onClick = {},
                        onClickToDetails = {}
                    )
                }
            }
        }

        // Verify the favorite icon shows the filled state
        composeTestRule.onNodeWithContentDescription("Toggle favorite city enabled").assertIsDisplayed()
    }
}