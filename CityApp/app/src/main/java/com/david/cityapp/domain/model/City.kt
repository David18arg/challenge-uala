package com.david.cityapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "cities"
)
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val country: String,
    val lat: Float,
    val lon: Float,
    val isFavorite: Boolean = false
)