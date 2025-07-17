package com.david.cityapp.presentation.ui.screens.citylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.david.cityapp.domain.model.City
import com.david.cityapp.domain.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: CityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _hasData = MutableStateFlow<Boolean?>(null)

    val citiesFlow = combine(
        _searchQuery,
    ) { query -> query
    }.flatMapLatest { (query) ->
        flowOf( repository.getCities(
            query = query,
            pageSize = 10
        ))
    }.cachedIn(viewModelScope)

    val uiState: StateFlow<CityListUiState> = combine(
        _searchQuery,
        _isLoading,
        _error,
        citiesFlow
    ) { values ->
        val searchQuery = values[0] as String
        val isLoading = values[2] as Boolean
        val error = values[3] as String?
        val cities = values[5] as List<City>

        CityListUiState(
            searchQuery = searchQuery,
            isLoading = isLoading,
            error = error,
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
}