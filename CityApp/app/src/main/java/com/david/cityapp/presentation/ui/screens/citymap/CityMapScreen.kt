package com.david.cityapp.presentation.ui.screens.citymap

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
    modifier: Modifier = Modifier.testTag("CityMapScreen")
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    LaunchedEffect(cityId) {
        viewModel.loadCity(cityId)
    }

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
        when {
            uiState.isLoading -> { Loading() }
            uiState.error != null -> { Message(uiState.error.toString()) }
            uiState.city != null -> {
                ScreenContent(
                    city = uiState.city!!,
                    mapCenter = uiState.mapCenter,
                    mapZoom = uiState.mapZoom,
                    onMapMoved = { center, zoom ->
                        viewModel.updateMapCenter(center, zoom)
                    },
                    context = LocalContext.current,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}