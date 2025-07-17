package com.david.cityapp.presentation.ui.screens.citylist

import com.david.cityapp.domain.model.City

data class CityListUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val cities: List<City> = emptyList()
)