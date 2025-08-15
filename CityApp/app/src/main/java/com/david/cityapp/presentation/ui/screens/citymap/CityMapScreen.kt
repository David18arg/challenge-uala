package com.david.cityapp.presentation.ui.screens.citymap

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.david.cityapp.presentation.common.components.Loading
import com.david.cityapp.presentation.common.components.Message
import com.david.cityapp.presentation.ui.components.TopBar
import com.david.cityapp.presentation.ui.screens.citymap.components.ScreenContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityMapScreen(
    cityId: Long,
    onBackClick: () -> Unit,
    viewModel: CityMapViewModel = hiltViewModel(),
    isLandscape: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(cityId) {
        viewModel.loadCity(cityId)
    }

    if (uiState.isLoading) Loading()
    else if (uiState.error != null) Message(uiState.error.toString())
    else {
        uiState.city?.let { city ->
            Scaffold(
                topBar = {
                    if (!isLandscape)
                        TopBar(
                            title = "${city.name}, ${city.country}",
                            onFavoriteClick = { },
                            onBackClick = onBackClick,
                            isFavorite = false,
                            showFavorites = false
                        )
                }
            ) { padding ->
                ScreenContent(
                    city = city,
                    mapCenter = uiState.mapCenter,
                    mapZoom = uiState.mapZoom,
                    onMapMoved = { center, zoom ->
                        viewModel.updateMapCenter(center, zoom)
                    },
                    isLandscape = isLandscape
                )
            }
        } ?: Message("No map data available")
    }
}