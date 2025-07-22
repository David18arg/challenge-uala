package com.david.cityapp.data.di

import com.david.cityapp.data.remote.CityApi
import com.david.cityapp.data.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CitiesApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeathersApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val CITIES_BASE_URL = "https://gist.githubusercontent.com/hernan-uala/dce8843a8edbe0b0018b32e137bc2b3a/raw/"
    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/"

    @CitiesApi
    @Provides
    @Singleton
    fun provideCitiesRetrofit(
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CITIES_BASE_URL)
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @WeathersApi
    @Provides
    @Singleton
    fun provideWeatherRetrofit(
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        val gson = com.google.gson.GsonBuilder()
            .setFieldNamingPolicy(com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideCityApi(
        @CitiesApi retrofit: Retrofit
    ): CityApi = retrofit.create(CityApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherApi(
        @WeathersApi retrofit: Retrofit
    ): WeatherApi = retrofit.create(WeatherApi::class.java)
}