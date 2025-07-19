package com.david.cityapp.presentation.ui.screens.citymap.components

import com.david.cityapp.domain.model.City
import org.osmdroid.util.GeoPoint

data class CityMapUiState(
    val city: City? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val mapCenter: GeoPoint = GeoPoint(0.0, 0.0),
    val mapZoom: Double = 15.0
)