package com.david.cityapp.data.di

import com.david.cityapp.domain.repository.CityRepository
import com.david.cityapp.domain.usecase.PreloadCitiesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides fun providePreloadCitiesUseCase(repo: CityRepository): PreloadCitiesUseCase =
        PreloadCitiesUseCase(repo)

}