package com.david.cityapp.domain.usecase

import com.david.cityapp.domain.repository.CityRepository
import javax.inject.Inject

class PreloadCitiesUseCase @Inject constructor(
    private val repo: CityRepository
) {
    /** Lanza el método de repositorio para cargar datos en Room */
    suspend operator fun invoke() {
        repo.preloadCities()
    }
}