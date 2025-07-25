package com.david.cityapp.presentation.ui.screens.citydetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.cityapp.domain.repository.CityRepository
import com.david.cityapp.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.let

@HiltViewModel
class CityDetailViewModel @Inject constructor(
    private val repository: CityRepository,
    private val weatherRepository: WeatherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(CityDetailUiState())
    val uiState: StateFlow<CityDetailUiState> = _uiState

    init {
        savedStateHandle.get<Long>("cityId")?.let { loadCity(it) }
    }

    fun loadCity(cityId: Long, forceReload: Boolean = false) {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val city = repository.getCityById(cityId)
                val weather = city?.let { weatherRepository.getWeatherByLocation(it.lat, city.lon) }
                Log.d("CityDetailViewModel", "Weather: $weather")
                if (weather != null) {
                    _uiState.update {
                        it.copy(
                            city = city,
                            weather = weather,
                            isFavorite = city.isFavorite,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { it.copy(error = "Ciudad no encontrada", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexion", isLoading = false) }
            }
        }
    }
}
