package com.david.cityapp.data.remote

import com.david.cityapp.data.model.CityResponse
import retrofit2.http.GET

interface CityApi {
    @GET("0996accf70cb0ca0e16f9a99e0ee185fafca7af1/cities.json")
    suspend fun fetchCitiesStream(): List<CityResponse>
}