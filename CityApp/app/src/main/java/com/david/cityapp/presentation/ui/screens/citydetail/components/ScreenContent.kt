package com.david.cityapp.presentation.ui.screens.citydetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.david.cityapp.domain.model.Weather

@Composable
fun ScreenContent(
    weather: Weather? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        weather?.let {
            MainCard(weather = it)
            Spacer(modifier = Modifier.height(16.dp))
            InfoCard(weather = it)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}