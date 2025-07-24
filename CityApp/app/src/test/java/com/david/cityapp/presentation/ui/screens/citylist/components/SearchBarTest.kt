package com.david.cityapp.presentation.ui.screens.citylist.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
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
class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `search bar displays correctly`() {

        composeTestRule.setContent {
            CityAppTheme {
                SearchBar(
                    query = "test",
                    onQueryChange = {},
                    modifier = Modifier
                )
            }
        }

        // Wait for the UI to settle
        composeTestRule.waitForIdle()

        // Verify search bar elements are displayed
        composeTestRule.onNodeWithTag("searchTextField").assertIsDisplayed()
    }

    @Test
    fun `search bar displays current query`() {
        val testQuery = "test query"

        composeTestRule.setContent {
            CityAppTheme {
                SearchBar(
                    query = testQuery,
                    onQueryChange = {},
                    modifier = Modifier
                )
            }
        }

        // Verify the search query is displayed in the text field
        composeTestRule.onNodeWithText(testQuery).assertIsDisplayed()
    }

    @Test
    fun `typing in search bar triggers onQueryChange`() {
        var capturedQuery = ""
        val testQuery = "test"

        composeTestRule.setContent {
            CityAppTheme {
                SearchBar(
                    query = capturedQuery,
                    onQueryChange = { capturedQuery = it },
                    modifier = Modifier
                )
            }
        }

        // Type in the search bar
        composeTestRule.onNodeWithTag("searchTextField")
            .performTextInput(testQuery)

        // Verify the callback was called with the correct query
        assert(capturedQuery == testQuery)
    }
}
