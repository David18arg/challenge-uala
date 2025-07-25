package com.david.cityapp.presentation.navigation.components

import android.preference.PreferenceManager
import androidx.core.content.edit
import com.david.cityapp.domain.usecase.PreloadCitiesUseCase
import javax.inject.Inject

class AppNavigationHelper @Inject constructor(
    private val preloadCities: PreloadCitiesUseCase
) {
    suspend fun preloadCitiesIfNeeded(context: android.content.Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            preloadCities()
            prefs.edit {
                putBoolean("is_first_launch", false)
                apply()
            }
        }
    }
}