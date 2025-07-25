package com.david.cityapp.presentation.ui.screens.citydetail

import com.david.cityapp.domain.model.City
import com.david.cityapp.domain.model.Weather

data class CityDetailUiState(
    val city: City? = null,
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val error: String? = null
)