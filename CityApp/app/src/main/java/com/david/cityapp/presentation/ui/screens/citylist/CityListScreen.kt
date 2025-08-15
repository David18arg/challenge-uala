package com.david.cityapp.presentation.ui.screens.citylist

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.components.Loading
import com.david.cityapp.presentation.common.components.Message
import com.david.cityapp.presentation.navigation.components.ScreenType
import com.david.cityapp.presentation.ui.components.TopBar
import com.david.cityapp.presentation.ui.screens.citylist.components.CityList
import com.david.cityapp.presentation.ui.screens.citylist.components.Landscape
import com.david.cityapp.presentation.ui.screens.citylist.components.SearchBar

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    city: City? = null,
    onCityClick: (City) -> Unit,
    onClickToDetails: (City) -> Unit,
    viewModel: CityListViewModel = hiltViewModel(),
    isLandscape: Boolean,
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentScreen by viewModel.selectedScreenType.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle(city)

    val cities = viewModel.citiesFlow.collectAsLazyPagingItems()
    val isLoading = cities.loadState.refresh is LoadState.Loading || uiState.isLoading
    val error = when (cities.loadState.refresh) {
        is LoadState.Error -> (cities.loadState.refresh as LoadState.Error).error.message
        else -> uiState.error
    }

    val onSelectedForCity = { city: City ->
        viewModel.selectCity(city)
        viewModel.selectScreenType(ScreenType.MAP)
        onCityClick(city)
    }

    val onSelectedForDetails = { city: City ->
        viewModel.selectCity(city)
        viewModel.selectScreenType(ScreenType.DETAIL)
        Log.d("ONSELECT","INGRESO PRIMERA VEZ: ${currentScreen}")
        onClickToDetails(city)
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "CIUDADES",
                subtitle = if (isLandscape && selectedCity != null) "${selectedCity?.name}, ${selectedCity?.country}" else null,
                onFavoriteClick = { viewModel.toggleFavoritesFilter() },
                onBackClick = {},
                isFavorite = uiState.showFavoritesOnly,
                showFavorites = true
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (!isLandscape) SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading && cities.itemCount == 0) {
                    Loading()
                } else if (error == null && !isLoading && uiState.showFavoritesOnly && cities.itemCount == 0) {
                    Message("No hay ciudades favoritas")
                } else if (error == null && !isLoading && !uiState.showFavoritesOnly && cities.itemCount == 0) {
                    Message("No hay ciudades disponibles")
                } else if (error != null && cities.itemCount == 0) {
                    Message(message = error)
                } else {
                    if (isLandscape) {
                        Landscape(
                            viewModel = viewModel,
                            cities = cities,
                            selectedCity = selectedCity,
                            currentScreen = currentScreen,
                            query = uiState.searchQuery,
                            onSelectedForDetails = onSelectedForDetails,
                            onSelectedForCity = onSelectedForCity,
                            isLandscape = isLandscape,
                            modifier = modifier
                        )
                    } else {
                        CityList(
                            cities = cities,
                            selectedCityId = selectedCity?.id,
                            onCityClick = onSelectedForCity,
                            onClickToDetails = onSelectedForDetails,
                            onToggleFavorite = { city ->
                                viewModel.onFavoriteToggled(city)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}