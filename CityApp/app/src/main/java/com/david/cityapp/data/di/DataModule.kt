package com.david.cityapp.data.di

import com.david.cityapp.data.local.dao.CityDao
import com.david.cityapp.data.remote.CityApi
import com.david.cityapp.data.repository.CityRepositoryImpl
import com.david.cityapp.domain.repository.CityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideCityRepository(
        dao: CityDao,
        api: CityApi,
    ): CityRepository {
        return CityRepositoryImpl(
            dao = dao,
            api = api
        )
    }
}
