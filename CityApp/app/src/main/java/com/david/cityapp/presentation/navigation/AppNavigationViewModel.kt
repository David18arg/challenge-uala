package com.david.cityapp.presentation.navigation

import androidx.lifecycle.ViewModel
import com.david.cityapp.presentation.navigation.components.AppNavigationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor(
    val appNavigationHelper: AppNavigationHelper
) : ViewModel()