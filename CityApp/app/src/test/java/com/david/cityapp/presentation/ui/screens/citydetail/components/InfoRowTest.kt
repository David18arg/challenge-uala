package com.david.cityapp.presentation.ui.screens.citydetail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
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
class InfoRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testImageUrl = "http://test.com/icon.png"
    private val testLabel = "Humidity"
    private val testValue = "65%"

    @Test
    fun `info row displays label and value`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                InfoRow(
                    imageUrl = testImageUrl,
                    label = testLabel,
                    value = testValue
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(testLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(testValue).assertIsDisplayed()
    }

    @Test
    fun `info row shows icon with correct content description`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                InfoRow(
                    imageUrl = testImageUrl,
                    label = testLabel,
                    value = testValue
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription(testLabel).assertIsDisplayed()
    }

    @Test
    fun `info row handles empty values`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                InfoRow(
                    imageUrl = testImageUrl,
                    label = testLabel,
                    value = ""
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("").assertExists()
    }

    @Test
    fun `info row applies custom modifier`() {
        // When
        composeTestRule.setContent {
            CityAppTheme {
                InfoRow(
                    imageUrl = testImageUrl,
                    label = testLabel,
                    value = testValue,
                    modifier = androidx.compose.ui.Modifier.padding(16.dp)
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(testLabel).assertIsDisplayed()
    }
}