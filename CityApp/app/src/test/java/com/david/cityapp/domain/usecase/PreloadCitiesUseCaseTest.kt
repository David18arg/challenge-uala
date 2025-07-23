package com.david.cityapp.domain.usecase

import com.david.cityapp.MainCoroutineRule
import com.david.cityapp.domain.repository.CityRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PreloadCitiesUseCaseTest {
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var useCase: PreloadCitiesUseCase
    private val mockRepository: CityRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        useCase = PreloadCitiesUseCase(mockRepository)
    }

    @Test
    fun `invoke calls repository preloadCities`() = runTest {
        // When
        useCase()

        // Then
        coVerify { mockRepository.preloadCities() }
    }
}