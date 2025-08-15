package com.david.cityapp.presentation.ui.screens.citylist.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.common.components.Message
import com.david.cityapp.presentation.navigation.components.ScreenType
import com.david.cityapp.presentation.ui.screens.citydetail.CityDetailScreen
import com.david.cityapp.presentation.ui.screens.citylist.CityListViewModel
import com.david.cityapp.presentation.ui.screens.citymap.CityMapScreen

@Composable
fun Landscape(
    viewModel: CityListViewModel,
    cities: LazyPagingItems<City>,
    selectedCity: City?,
    currentScreen: ScreenType?,
    query: String,
    onSelectedForDetails: (City) -> Unit,
    onSelectedForCity: (City) -> Unit,
    isLandscape: Boolean,
    modifier: Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Panel izquierdo: Lista de ciudades
        Box(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                SearchBar(
                    query = query,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth()
                )
                CityList(
                    cities = cities,
                    selectedCityId = selectedCity?.id,
                    onCityClick = onSelectedForCity,
                    onClickToDetails = onSelectedForDetails,
                    onToggleFavorite = { city ->
                        viewModel.onFavoriteToggled(city)
                    },
                    modifier = modifier.padding( 8.dp)
                )
            }
        }

        // Panel derecho: Mapa o Detalle de la ciudad
        Box(
            modifier = modifier
                .weight(0.6f)
                .fillMaxHeight()
        ) {
            LaunchedEffect(selectedCity, currentScreen) {
                // Este efecto se disparará cuando cambie selectedCity o currentScreen
            }
            Log.d("ONSELECT","INGRESO AL LANDSCAPE CON EL TIPO DE PANTALLA: ${currentScreen}")
            when (currentScreen) {
                ScreenType.DETAIL -> {
                    if (selectedCity != null) {
                        CityDetailScreen(
                            cityId = selectedCity!!.id,
                            onBackClick = { },
                            isLandscape = isLandscape
                        )
                    } else {
                        Message("Selecciona una ciudad para ver los detalles")
                    }
                }
                ScreenType.MAP -> {
                    if (selectedCity != null) {
                        CityMapScreen(
                            cityId = selectedCity!!.id,
                            onBackClick = { },
                            isLandscape = isLandscape
                        )
                    } else {
                        Message("Selecciona una ciudad para ver el mapa")
                    }
                }
                else -> {
                    Message("Selecciona una ciudad para comenzar")
                }
            }
        }
    }
}