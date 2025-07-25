package com.david.cityapp.presentation.ui.screens.citydetail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import com.david.cityapp.presentation.common.components.Loading
import com.david.cityapp.presentation.common.components.Message
import com.david.cityapp.presentation.ui.components.TopBar
import com.david.cityapp.presentation.ui.screens.citydetail.components.ScreenContent

@Composable
fun CityDetailScreen(
    cityId: Long,
    onBackClick: () -> Unit,
    viewModel: CityDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    LaunchedEffect(cityId) {
        viewModel.loadCity(cityId)
    }

    if (uiState.isLoading && uiState.weather == null) {
        Loading()
    } else if (uiState.error != null) {
        Message(uiState.error.toString())
    } else {
        uiState.weather?.let { weather ->
            Scaffold(
                topBar = {
                    TopBar(
                        title = "${uiState.city?.name}, ${uiState.city?.country}",
                        onFavoriteClick = { },
                        onBackClick = onBackClick,
                        isFavorite = false,
                        showFavorites = false,
                        showBackArrow = !isLandscape
                    )
                }
            ) { padding ->
                ScreenContent(
                    weather = weather,
                    modifier = Modifier.padding(padding)
                )
            }
        } ?: Message("No weather data available")
    }
}