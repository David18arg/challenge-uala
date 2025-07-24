package com.david.cityapp.presentation.ui.screens.citylist.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.theme.CityAppTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(
    sdk = [33]
)
class CityListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCities = listOf(
        City(
            id = 1,
            name = "Test City 1",
            country = "Test Country 1",
            lat = 0.0f,
            lon = 0.0f,
            isFavorite = false
        ),
        City(
            id = 2,
            name = "Test City 2",
            country = "Test Country 2",
            lat = 1.0f,
            lon = 1.0f,
            isFavorite = true
        )
    )

    @Test
    fun `city list displays all cities`() {
        val pagingData = PagingData.from(testCities)

        composeTestRule.setContent {
            CityAppTheme {
                val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()
                CityList(
                    cities = lazyPagingItems,
                    onCityClick = {},
                    onToggleFavorite = {},
                    onClickToDetails = {},
                    modifier = Modifier,
                    selectedCityId = null
                )
            }
        }

        // Wait for the UI to be idle
        composeTestRule.waitForIdle()

        // Verify all cities are displayed with the format "City, Country"
        testCities.forEach { city ->
            composeTestRule.onNodeWithText("${city.name}, ${city.country}").assertIsDisplayed()
        }
    }

    @Test
    fun `city list shows empty state when no cities`() {
        val emptyPagingData = PagingData.empty<City>()

        composeTestRule.setContent {
            CityAppTheme {
                val lazyPagingItems = flowOf(emptyPagingData).collectAsLazyPagingItems()
                CityList(
                    cities = lazyPagingItems,
                    onCityClick = {},
                    onToggleFavorite = {},
                    onClickToDetails = {},
                    modifier = Modifier,
                    selectedCityId = null
                )
            }
        }

        // Wait for the UI to be idle
        composeTestRule.waitForIdle()

        // Verify no city items are shown
        testCities.forEach { city ->
            composeTestRule.onNodeWithText("${city.name}, ${city.country}").assertDoesNotExist()
        }
    }

    @Test
    fun `clicking on city triggers onCityClick`() {
        val pagingData = PagingData.from(testCities)
        var clickedCity: City? = null

        composeTestRule.setContent {
            CityAppTheme {
                val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()
                CityList(
                    cities = lazyPagingItems,
                    onCityClick = { onClickCity -> clickedCity = onClickCity },
                    onToggleFavorite = {},
                    onClickToDetails = {},
                    modifier = Modifier,
                    selectedCityId = null
                )
            }
        }

        // Wait for the UI to be idle
        composeTestRule.waitForIdle()

        // Click on the first city
        composeTestRule.onNodeWithText("${testCities[0].name}, ${testCities[0].country}").performClick()

        // Verify the callback was called with the correct city
        assert(clickedCity == testCities[0])
    }

    @Test
    fun `clicking favorite icon triggers onFavoriteClick`() {
        val pagingData = PagingData.from(testCities)
        var favoriteToggledCity: City? = null

        composeTestRule.setContent {
            CityAppTheme {
                val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()
                CityList(
                    cities = lazyPagingItems,
                    onCityClick = {},
                    onToggleFavorite = { onToggleFavorite -> favoriteToggledCity = onToggleFavorite },
                    onClickToDetails = {},
                    modifier = Modifier,
                    selectedCityId = null
                )
            }
        }
        // Wait for the UI to be idle
        composeTestRule.waitForIdle()

        // Click on the favorite icon of the first city
        composeTestRule
            .onAllNodesWithContentDescription("Toggle favorite city")
            .onFirst()
            .performClick()

        // Verify the callback was called with the correct city and favorite state
        assert(favoriteToggledCity == testCities[0])
    }
}
