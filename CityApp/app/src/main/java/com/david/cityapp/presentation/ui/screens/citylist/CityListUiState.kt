package com.david.cityapp.presentation.ui.screens.citylist

import androidx.paging.PagingData
import com.david.cityapp.domain.model.City
import com.david.cityapp.presentation.navigation.components.ScreenType

data class CityListUiState(
    val searchQuery: String = "",
    val showFavoritesOnly: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCity: City? = null,
    val selectedScreenType: ScreenType? = null,
    val cities: PagingData<City> = PagingData.empty()
)