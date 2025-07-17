package com.david.cityapp.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CitiesApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}