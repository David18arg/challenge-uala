package com.david.cityapp.presentation.ui.screens.citymap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.cityapp.domain.repository.CityRepository
import com.david.cityapp.presentation.ui.screens.citymap.components.CityMapUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class CityMapViewModel @Inject constructor(
    private val repository: CityRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(CityMapUiState())
    val uiState: StateFlow<CityMapUiState> = _uiState

    init {
        savedStateHandle.get<Long>("cityId")?.let { loadCity(it) }
    }

    fun loadCity(cityId: Long) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val city = repository.getCityById(cityId)
                if (city != null) {
                    _uiState.update {
                        it.copy(
                            city = city,
                            mapCenter = GeoPoint(city.lat.toDouble(), city.lon.toDouble()),
                            mapZoom = 15.0,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { it.copy(error = "City not found", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error: ${e.message}", isLoading = true) }
            }
        }
    }

    fun updateMapCenter(center: GeoPoint, zoom: Double = _uiState.value.mapZoom) {
        _uiState.update { it.copy(mapCenter = center, mapZoom = zoom) }
    }
}