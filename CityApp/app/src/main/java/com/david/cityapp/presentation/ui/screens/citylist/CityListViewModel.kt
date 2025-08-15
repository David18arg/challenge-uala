package com.david.cityapp.presentation.ui.screens.citylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.david.cityapp.domain.model.City
import com.david.cityapp.domain.repository.CityRepository
import com.david.cityapp.presentation.navigation.components.ScreenType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: CityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _showFavoritesOnly = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _selectedCity = MutableStateFlow<City?>(null)
    private val _selectedScreenType = MutableStateFlow<ScreenType?>(null)
    private val _hasData = MutableStateFlow<Boolean?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()
    val selectedScreenType: StateFlow<ScreenType?> = _selectedScreenType.asStateFlow()

    val citiesFlow = combine(
        _searchQuery,
        _showFavoritesOnly
    ) { query, showFavoritesOnly ->
        Pair(query, showFavoritesOnly)
    }.flatMapLatest { (query, showFavoritesOnly) ->
        repository.getCities(
            query = query,
            onlyFav = showFavoritesOnly,
            pageSize = 10
        )
    }.cachedIn(viewModelScope)

    val uiState: StateFlow<CityListUiState> = combine(
        _searchQuery,
        _showFavoritesOnly,
        _isLoading,
        _error,
        _selectedCity,
        _selectedScreenType,
        citiesFlow
    ) { values ->
        val searchQuery = values[0] as String
        val showFavoritesOnly = values[1] as Boolean
        val isLoading = values[2] as Boolean
        val error = values[3] as String?
        val selectedCity = values[4] as City?
        val selectedScreenType = values[5] as ScreenType?
        val cities = values[6] as PagingData<City>

        CityListUiState(
            searchQuery = searchQuery,
            showFavoritesOnly = showFavoritesOnly,
            isLoading = isLoading,
            error = error,
            selectedCity = selectedCity,
            selectedScreenType = selectedScreenType,
            cities = cities
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CityListUiState()
    )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _hasData.value = true
            } catch (e: Exception) {
                _error.value = "Error loading cities"
                _hasData.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun onFavoriteToggled(city: City) {
        viewModelScope.launch {
            repository.toggleFavorite(city.id)
            if (_selectedCity.value?.id == city.id) {
                _selectedCity.value = _selectedCity.value?.copy(
                    isFavorite = !city.isFavorite
                )
            }
        }
    }

    fun selectCity(city: City?) {
        _selectedCity.value = city
    }

    fun selectScreenType(screenType: ScreenType?) {
        _selectedScreenType.value = screenType
    }
}
